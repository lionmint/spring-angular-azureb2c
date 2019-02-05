package com.myapi.configuration;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.myapi.auth.*;
import com.myapi.utils.AzureADUtils;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	@Resource
    private UserDetailsService userDetailsService;
	
	@Resource
	private AzureADUtils azureADUtils;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter(userDetailsService,azureADUtils);
    }
    
    @Bean
    public RegistrationBean jwtAuthFilterRegister(JwtAuthenticationTokenFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
        //X-Frame-Options enable
        http.headers()
        	.frameOptions().sameOrigin()
        	.httpStrictTransportSecurity().disable();
       
        //AZURE TOKEN
        http
        	.csrf().disable()
        	.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        	.authorizeRequests().anyRequest().fullyAuthenticated();
        
        // Custom JWT based security filter
        http
            .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        
    }
	
	@Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/resources/**","/static/**"); // #3
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
