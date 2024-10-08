package com.sgcu65.assignment.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.dto.ChangePasswordDto;
import com.sgcu65.assignment.message.JsonFieldName;
import com.sgcu65.assignment.service.JwtService;
import com.sgcu65.assignment.service.UserService;

@RestController
public class UserController {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@GetMapping(value = "/api/v1/users")
	public ResponseEntity<Map<String, Object>> findAll(@RequestHeader("Authorization") String bearerToken,@RequestParam(value = "firstName",required = false) String firstName,
			@RequestParam(value = "surName",required = false) String surName,
			@RequestParam(value = "position",required = false) String position,
			@RequestParam(value = "salary",required = false) String salary
			) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.findAll(firstName,surName,position,salary,loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);

	}

	@PostMapping(value = "/api/v1/users")
	public ResponseEntity<Map<String, Object>> save(@RequestHeader("Authorization") String bearerToken,
			@RequestBody User user) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.save(user, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);
	}

	@GetMapping(value = "/api/v1/users/{id}")
	public ResponseEntity<Map<String, Object>> findById(@RequestHeader("Authorization") String bearerToken,
			@PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.findById(id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);

	}

	@PutMapping(value = "/api/v1/users/{id}")
	public ResponseEntity<Map<String, Object>> update(@RequestHeader("Authorization") String bearerToken,
			@RequestBody User entity, @PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.update(entity, id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);
	}

	@DeleteMapping(value = "/api/v1/users/{id}")
	public ResponseEntity<Map<String, Object>> delete(@RequestHeader("Authorization") String bearerToken,
			@PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.delete(id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);
	}
	
	@PatchMapping(value = "/api/v1/users")
	public ResponseEntity<Map<String,Object>> changePassword(@RequestHeader("Authorization") String bearerToken,@RequestBody ChangePasswordDto dto){
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = userService.changePassword(dto, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);
	}
	@GetMapping(value = "/api/v1/users/me")
	public ResponseEntity<Map<String,Object>> me(@RequestHeader("Authorization") String bearerToken){
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String,Object> map = userService.me(loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);
	}

}
