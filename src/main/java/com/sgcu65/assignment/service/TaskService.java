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
import com.sgcu65.assignment.domain.TaskStatus;
import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.message.ErrorMessage;
import com.sgcu65.assignment.message.JsonFieldName;
import com.sgcu65.assignment.repository.ITaskRepository;
import com.sgcu65.assignment.repository.IUserRepository;

@Service
public class TaskService {
	@Autowired
	private ITaskRepository taskRepository;
	
	@Autowired
	private IUserRepository userRepository;
	public Map<String,Object> findAll(String loginUser){
		Map<String,Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if(!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			List<Task> tasks = taskRepository.findAll();
			if(tasks.size()==0) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TASK_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, tasks);
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	@Transactional
	public Map<String,Object> save(Task entity,String loginUser){
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
				errors.add(ErrorMessage.TASK_NAME_IS_REQUIRED);
			}
			if(entity.getContent().isEmpty() || entity.getContent().equals("")) {
				errors.add(ErrorMessage.TASK_CONTENT_IS_REQUIRED);
			}
			if(entity.getDeadline()==null) {
				errors.add(ErrorMessage.TASK_CONTENT_IS_REQUIRED);
			}
			if(errors.size()>0) {
				map.put(JsonFieldName.CODE,HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,String.join(",",errors));
				return map;
			}
			entity.setTaskStatus(TaskStatus.InProgress);
			Set<User> users = entity.getUsers().stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			User user = userRepository.findByEmail(loginUser).get();
			users.add(user);
			entity.setUsers(users);
			
			Task task = taskRepository.save(entity);
			
			Task ret = taskRepository.findById(task.getId()).get();
			map.put(JsonFieldName.CODE, HttpStatus.CREATED.value());
			map.put(JsonFieldName.DATA, ret);
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
			Optional<Task> task = taskRepository.findById(UUID.fromString(id));
			if(task.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.TASK_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, task.get());
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE,HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	@Transactional
	public Map<String,Object> update(Task entity,String id,String loginUser){
		System.out.println(loginUser);
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
				errors.add(ErrorMessage.TASK_NAME_IS_REQUIRED);
			}
			if(entity.getContent().isEmpty() || entity.getContent().equals("")) {
				errors.add(ErrorMessage.TASK_CONTENT_IS_REQUIRED);
			}
			if(entity.getDeadline()==null) {
				errors.add(ErrorMessage.TASK_CONTENT_IS_REQUIRED);
			}
			if(entity.getTaskStatus()==null) {
				errors.add(ErrorMessage.TASK_STATUS_IS_REQUIRED);
			}
			if(errors.size()>0) {
				map.put(JsonFieldName.CODE,HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,String.join(",",errors));
				return map;
			}
			Optional<Task> taskDb  = taskRepository.findById(UUID.fromString(id));
			if(taskDb.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.TASK_NOT_EXIST );
				return map;
			}
			entity.setId(UUID.fromString(id));
			Set<User> users = entity.getUsers().stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			entity.setUsers(users);
			Task task = taskRepository.save(entity);
			Task ret = taskRepository.findById(task.getId()).get();
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, ret);
	
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
			Optional<Task> taskDb  = taskRepository.findById(UUID.fromString(id));
			if(taskDb.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.TASK_NOT_EXIST );
				return map;
			}
			Task task = taskDb.get();
			task.getUsers().clear();
			taskRepository.delete(task);
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, ErrorMessage.SUCCESS);
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
			Optional<Task> task = taskRepository.findById(UUID.fromString(id));
			if(task.isEmpty()) {
				map.put(JsonFieldName.CODE,HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.TASK_NOT_EXIST);
				return map;
			}
			Set<User> items = users.stream().map(t->{
				User item = userRepository.findById(t.getId()).get();
				return item;
			}).collect(Collectors.toSet());
			task.get().setUsers(items);
			taskRepository.save(task.get());
			Task ret = taskRepository.findById(UUID.fromString(id)).get();
			map.put(JsonFieldName.CODE,HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, ret);
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
