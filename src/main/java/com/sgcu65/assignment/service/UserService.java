package com.sgcu65.assignment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sgcu65.assignment.domain.Role;
import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.dto.ChangePasswordDto;
import com.sgcu65.assignment.message.ErrorMessage;
import com.sgcu65.assignment.message.JsonFieldName;
import com.sgcu65.assignment.repository.IUserRepository;
import com.sgcu65.assignment.utils.PasswordUtils;

@Service
public class UserService {

	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public Map<String, Object> findAll(String firstName, String surName, String position, String salary,
			String loginUser) {
		Map<String, Object> map = new HashMap<>();
		try {
			boolean isAdmin = IsAdmin(loginUser);
			if (!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}
			if (firstName == null || firstName.isEmpty() || firstName.equals("")) {
				firstName = "";
			}
			if (surName == null || surName.isEmpty() || surName.equals("")) {
				surName = "";
			}
			Role role;
			if (position == null || position.isEmpty() || position.equals("")) {
				role = null;
			} else {
				role = Role.valueOf(position);

			}
			Double s;
			if (salary == null || salary.isEmpty() || salary.equals("")) {
				s=null;
			}else {
				try {
					s = Double.parseDouble(salary);
				} catch (Exception ex2) {
					s = -1.0;
				}
			}
			

			List<User> users = userRepository.findAll(firstName.replace("*", "%").replace("?", "_"),
					surName.replace("*", "%").replace("?", "_"), role, s);

			if (users.size() == 0) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, users);
			return map;
		} catch (Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}

	}

	public Map<String, Object> save(User entity, String loginUser) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (!loginUser.equals("SUPER")) {
				boolean isAdmin = IsAdmin(loginUser);
				if (!isAdmin) {
					map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
					map.put(JsonFieldName.ERROR, ErrorMessage.USER_IS_UN_AUTHOIRZE);
					return map;
				}
			}

			List<String> errors = validate(entity);
			if (errors.size() > 0) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, String.join(",", errors));
				return map;
			}
			Optional<User> userDbs = userRepository.findByEmail(entity.getEmail());
			if (userDbs.isEmpty() == false) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.EMAIL_DUPLICATE);
				return map;
			}
			entity.setPassword(PasswordUtils.encode(entity.getPassword()));
			User ret = userRepository.save(entity);
			map.put(JsonFieldName.CODE, HttpStatus.CREATED.value());
			map.put(JsonFieldName.DATA, ret);
			return map;
		} catch (Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}

	public Map<String, Object> findById(String id, String loginUser) {
		Map<String, Object> map = new HashMap<>();
		try {

			boolean isAdmin = IsAdmin(loginUser);
			if (!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}

			Optional<User> user = userRepository.findById(UUID.fromString(id));
			if (user.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_NOT_FOUND);
				return map;
			}
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, user.get());
			return map;
		} catch (Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}

	public Map<String, Object> update(User entity, String id, String loginUser) {
		Map<String, Object> map = new HashMap<>();
		try {

			boolean isAdmin = IsAdmin(loginUser);
			if (!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}

			List<String> errors = validate(entity);
			if (errors.size() > 0) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, String.join(",", errors));
				return map;
			}
			Optional<User> userDbs = userRepository.findByEmail(entity.getEmail());
			if (userDbs.isEmpty() == false) {
				if (!userDbs.get().getId().equals(UUID.fromString(id))) {
					map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
					map.put(JsonFieldName.ERROR, ErrorMessage.EMAIL_DUPLICATE);
					return map;
				}

			}
			Optional<User> userDb = userRepository.findById(UUID.fromString(id));
			if (userDb.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_NOT_EXIST);
				return map;

			}
			entity.setId(UUID.fromString(id));
			entity.setPassword(PasswordUtils.encode(entity.getPassword()));
			userRepository.save(entity);
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, entity);
			return map;
		} catch (Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}

	public Map<String, Object> delete(String id, String loginUser) {
		Map<String, Object> map = new HashMap<>();
		try {

			boolean isAdmin = IsAdmin(loginUser);
			if (!isAdmin) {
				map.put(JsonFieldName.CODE, HttpStatus.UNAUTHORIZED.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_IS_UN_AUTHOIRZE);
				return map;
			}

			Optional<User> userDb = userRepository.findById(UUID.fromString(id));
			if (userDb.isEmpty()) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.USER_NOT_EXIST);
				return map;

			}
			userRepository.delete(userDb.get());
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, ErrorMessage.SUCCESS);
			return map;

		} catch (Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	public Map<String,Object> changePassword(ChangePasswordDto dto ,String loginUser){
		Map<String, Object> map = new HashMap<>();
		try {

			List<String> errors = new ArrayList<>();
			if(dto.getOldPassword()==null || dto.getOldPassword().isEmpty()|| dto.getOldPassword().equals("")) {
				errors.add(ErrorMessage.OLD_PASSWORD_IS_REQUIRED);
			}
			if(dto.getNewPassword()==null || dto.getNewPassword().isEmpty()|| dto.getNewPassword().equals("")) {
				errors.add(ErrorMessage.NEW_PASSWORD_IS_REQUIRED);
			}
			if(errors.size()>0) {
				map.put(JsonFieldName.CODE,HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,String.join(",",errors));
				return map;
			}
			if(!dto.getNewPassword().equals(dto.getNewPassword())) {
				map.put(JsonFieldName.CODE, HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.OLD_PASSWORD_IS_NOT_EQUAL_NEW_PASSWORD);
				return map;
			}
			Optional<User> user = userRepository.findByEmail(loginUser);
			if(user.isEmpty()) {
				map.put(JsonFieldName.CODE,HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_NOT_EXIST);
				return map;
			}
			if(!passwordEncoder.matches(dto.getOldPassword(),user.get().getPassword())) {
				map.put(JsonFieldName.CODE,HttpStatus.BAD_REQUEST.value());
				map.put(JsonFieldName.ERROR, ErrorMessage.PASSWORD_NOT_MATCH);
				return map;
			}
			user.get().setPassword(PasswordUtils.encode(dto.getNewPassword()));
			userRepository.save(user.get());
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, user.get());
			return map;
			
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	public Map<String,Object> me(String loginUser){
		Map<String, Object> map = new HashMap<>();
		try {
			Optional<User> user= userRepository.findByEmail(loginUser);
			if(user.isEmpty()) {
				map.put(JsonFieldName.CODE,HttpStatus.NOT_FOUND.value());
				map.put(JsonFieldName.ERROR,ErrorMessage.USER_NOT_EXIST);
				return map;
			}
			map.put(JsonFieldName.CODE, HttpStatus.OK.value());
			map.put(JsonFieldName.DATA, user.get());
			return map;
		}catch(Exception ex) {
			map.put(JsonFieldName.CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
			map.put(JsonFieldName.ERROR, ex.getMessage());
			return map;
		}
	}
	private List<String> validate(User entity){
		List<String> errors = new ArrayList<>();
		if (entity.getEmail() == null || entity.getEmail().isEmpty()) {
			errors.add(ErrorMessage.EMAIL_IS_REQUIRED);
		}
		if (entity.getFirstName() == null || entity.getFirstName().isEmpty()) {
			errors.add(ErrorMessage.FIRST_NAME_IS_REQUIRED);
		}
		if (entity.getSurName() == null || entity.getSurName().isEmpty()) {
			errors.add(ErrorMessage.SUR_NAME_IS_REQUIRED);
		}
		if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
			errors.add(ErrorMessage.PASSWORD_IS_REQUIRED);
		}
		if(entity.getSalary()==null) {
			errors.add(ErrorMessage.SALARY_IS_REQUIRED);
		}
		if (entity.getRole() == null) {
			errors.add(ErrorMessage.ROLE_IS_REQUIRED);
		}
		return errors;
	}

	private boolean IsAdmin(String loginUser) {
		Optional<User> loginData = userRepository.findByEmail(loginUser);
		return !loginData.isEmpty() && loginData.get().getRole() == Role.Admin;

	}
}
