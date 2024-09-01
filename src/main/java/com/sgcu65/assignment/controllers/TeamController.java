package com.sgcu65.assignment.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sgcu65.assignment.domain.Team;
import com.sgcu65.assignment.message.JsonFieldName;
import com.sgcu65.assignment.service.JwtService;
import com.sgcu65.assignment.service.TeamService;

@RestController
public class TeamController {

	@Autowired
	private TeamService teamService;
	
	@Autowired
	private JwtService jwtService;
	
	
	@GetMapping(value = "/api/v1/teams")
	public ResponseEntity<Map<String,Object>> findAll(@RequestHeader("Authorization") String bearerToken){
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String,Object> map = teamService.findAll(loginUser);
		return ResponseEntity.status((int)map.get(JsonFieldName.CODE)).body(map);
	}
	@PostMapping(value = "/api/v1/teams")
	public ResponseEntity<Map<String,Object>> save(@RequestHeader("Authorization") String bearerToken,@RequestBody Team entity){
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String,Object> map = teamService.save(entity, loginUser);
		return ResponseEntity.status((int)map.get(JsonFieldName.CODE)).body(map);
	}
	
	@GetMapping(value = "/api/v1/teams/{id}")
	public ResponseEntity<Map<String, Object>> findById(@RequestHeader("Authorization") String bearerToken,
			@PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		System.out.println(jwt);
		String loginUser = jwtService.extractUsername(jwt);
		System.out.println(loginUser);
		Map<String, Object> map = teamService.findById(id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);

	}
	
	@PutMapping(value = "/api/v1/teams/{id}")
	public ResponseEntity<Map<String, Object>> update(@RequestHeader("Authorization") String bearerToken,
		@RequestBody Team entity,	@PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = teamService.update(entity, id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);

	}
	
	@DeleteMapping(value = "/api/v1/teams/{id}")
	public ResponseEntity<Map<String, Object>> delete(@RequestHeader("Authorization") String bearerToken,
			@PathVariable String id) {
		final String jwt = bearerToken.substring(7);
		String loginUser = jwtService.extractUsername(jwt);
		Map<String, Object> map = teamService.delete(id, loginUser);
		return ResponseEntity.status((int) map.get(JsonFieldName.CODE)).body(map);

	}
}
