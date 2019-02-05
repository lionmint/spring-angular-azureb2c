package com.myapi.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	//YOU CAN CONNECT THIS TO A USER REPOSITORY
    	boolean isNotValid = true;
    	switch(username) {
    		case "jose@lionmint.onmicrosoft.com" : isNotValid = false;
    										  break;
    	}
        if (isNotValid) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(username, createHashCode(username), getAuthorities());
    }
    
	private Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>(1);
		authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
		return authorities;
	}
	
	private String createHashCode(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}
}
