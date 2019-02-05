package com.myapi.auth;

import static com.myapi.constants.SecurityConstants.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.myapi.exception.TokenException;
import com.myapi.utils.AzureADUtils;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    private UserDetailsService userDetailsService;
    private AzureADUtils azureADUtils;
    
    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, AzureADUtils azureADUtils) {
		super();
		this.userDetailsService = userDetailsService;
		this.azureADUtils = azureADUtils;
	}

	private int tokenType(HttpServletRequest request) {
    	int tokenType = 0;
    	final String requestHeader = request.getHeader(HEADER_STRING);
    	final String apiKeyHeader = request.getHeader(HEADER_TOKEN_STRING);
    	if(requestHeader != null && requestHeader.startsWith(TOKEN_PREFIX)) {
    		tokenType = 1;  //AZURE TOKEN
    	}
    	return tokenType;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestHeader = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        int type = tokenType(request);
        
        switch(type) {
        	case 0: logger.warn("Will ignore the header");
        			break;
        	case 1: requestHeader = request.getHeader(HEADER_STRING);
        			authToken = requestHeader.substring(7);
        			try {
        				username = azureADUtils.getUsernameFromToken(authToken);
        			}catch (TokenException e) {
		                logger.warn("the token is expired and not valid anymore", e);
		    		}
        			break;
        }

        logger.info("checking authentication for user " + username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        	UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // For simple validation it is completely sufficient to just check the token integrity
            if (userDetails != null && (type == 1 && azureADUtils.validateToken(authToken)) ) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}