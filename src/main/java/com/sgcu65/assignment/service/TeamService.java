package com.sgcu65.assignment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgcu65.assignment.domain.Role;
import com.sgcu65.assignment.domain.Task;
import com.sgcu65.assignment.domain.Team;
import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.message.ErrorMessage;
import com.sgcu65.assignment.message.JsonFieldName;
import com.sgcu65.assignment.repository.ITaskRepository;
import com.sgcu65.assignment.repository.ITeamRepository;
import com.sgcu65.assignment.repository.IUserRepository;

@Service
public class TeamService {

	@Autowired
	private ITeamRepository teamRepository;
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private ITaskRepository taskRepository;
	
	
	public Map<String,Object> findAll(String name,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			if(name==null || name.isEmpty() || name.equals("")) {
				name = "";
			}
			List<Team> teams = teamRepository.findAll(name.replace("*", "%").replace("?", "_"));
			if(teams.size()==0) {
				map.put(JsonFieldName.CODE,HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA,teams);
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	@Transactional
	public Map<String,Object> save(Team entity,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			List<String> errors = new ArrayList<>();
			if(entity.getName().isEmpty() || entity.getName().equals("")) {
				errors.add(ErrorMessage.NAME_IS_REQUIRED);
			}
			if(errors.size()>0) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,String.join(",",errors));
				return map;
			}
			Set<User> users = entity.getUsers().stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			User user = userRepository.findByEmail(loginUser).get();
			users.add(user);
			Set<Task> tasks = entity.getTasks().stream().map(t->{
				Task item = taskRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			entity.setTasks(tasks);
			entity.setUsers(users);
			Team team = teamRepository.save(entity);
			Team ret= teamRepository.findById(team.getId()).get();
			map.put(JsonFieldName.CODE,HttpStatus.CREATED.value());
			map.put(JsonFieldName.DATA,ret);
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	
	public Map<String,Object> findById(String id,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			Optional<Team> team = teamRepository.findById(UUID.fromString(id));
			if(team.isEmpty()) {
				map.put(JsonFieldName.CODE,HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, team.get());
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	@Transactional
	public Map<String,Object> update(Team entity,String id,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			List<String> errors = new ArrayList<>();
			if(entity.getName().isEmpty() || entity.getName().equals("")) {
				errors.add(ErrorMessage.NAME_IS_REQUIRED);
			}
			if(errors.size()>0) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,String.join(",",errors));
				return map;
			}
			Optional<Team> teamDb = teamRepository.findById(UUID.fromString(id));
			if(teamDb.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_IS_NOT_EXIST);
				return map;
			}
			entity.setId(UUID.fromString(id));
			
			Set<User> users = entity.getUsers().stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			Set<Task> tasks = entity.getTasks().stream().map(t->{
				Task item = taskRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			entity.setTasks(tasks);
			entity.setUsers(users);
			Team team = teamRepository.save(entity);
			Team ret= teamRepository.findById(team.getId()).get();
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA,ret);
			return map;
			
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	@Transactional
	public Map<String,Object> delete(String id,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			Optional<Team> team = teamRepository.findById(UUID.fromString(id));
			if(team.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_IS_NOT_EXIST);
				return map;
			}
			team.get().getUsers().clear();
			team.get().getTasks().clear();
			teamRepository.delete(team.get());
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA,ErrorMessage.SUCCESS);
			return map;
			
			
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	
	@Transactional
	public Map<String,Object> assignUser(Set<User> users,String id,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			Optional<Team> team = teamRepository.findById(UUID.fromString(id));
			if(team.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_IS_NOT_EXIST);
				return map;
			}
			Set<User> items = users.stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			team.get().setUsers(items);
			teamRepository.save(team.get());
			Team ret= teamRepository.findById(UUID.fromString(id)).get();
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA,ret);
			return map;
			
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	
	@Transactional
	public Map<String,Object> assignTask(Set<Task> tasks,String id,String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			Optional<Team> team = teamRepository.findById(UUID.fromString(id));
			if(team.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TEAM_IS_NOT_EXIST);
				return map;
			}
			Set<Task> items = tasks.stream().map(t->{
				Task item = taskRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			team.get().setTasks(items);
			teamRepository.save(team.get());
			Team ret= teamRepository.findById(UUID.fromString(id)).get();
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA,ret);
			return map;
			
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	
	private boolean IsAdmin(String loginUser) {
		Optional<User> loginData = userRepository.findByEmail(loginUser);
		return !loginData.isEmpty() && loginData.get().getRole() == Role.Admin;

	}
}
