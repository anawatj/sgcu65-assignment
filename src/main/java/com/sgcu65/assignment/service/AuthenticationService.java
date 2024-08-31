package com.sgcu65.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.dto.LoginUserDto;
import com.sgcu65.assignment.repository.IUserRepository;

@Service
public class AuthenticationService {

	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	   public User authenticate(LoginUserDto input) {
		     authenticationManager.authenticate(
		                new UsernamePasswordAuthenticationToken(
		                        input.getEmail(),
		                        input.getPassword()
		                )
		        );

		        return userRepository.findByEmail(input.getEmail())
		                .orElseThrow();
	    }
}
