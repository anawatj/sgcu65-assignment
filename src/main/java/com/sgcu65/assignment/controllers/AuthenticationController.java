package com.sgcu65.assignment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.dto.LoginResponse;
import com.sgcu65.assignment.dto.LoginUserDto;
import com.sgcu65.assignment.service.AuthenticationService;
import com.sgcu65.assignment.service.JwtService;

@RestController
public class AuthenticationController {
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/api/v1/auth/login")
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
		User authenticatedUser = authenticationService.authenticate(loginUserDto);

		String jwtToken = jwtService.generateToken(authenticatedUser);

		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(jwtToken);
		loginResponse.setExpiresIn(jwtService.getExpirationTime());

		return ResponseEntity.ok(loginResponse);
	}
}
