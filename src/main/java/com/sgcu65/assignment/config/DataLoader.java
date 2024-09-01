package com.sgcu65.assignment.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sgcu65.assignment.domain.Role;
import com.sgcu65.assignment.domain.User;
import com.sgcu65.assignment.service.UserService;

@Component
public class DataLoader implements ApplicationRunner {

	@Autowired
	private UserService userService;
	@Override
	public void run(ApplicationArguments args) throws Exception {
		User user = new User();
		user.setEmail("ajarusiripot@gmail.com");
		user.setPassword("P@ssw0rd");
		user.setFirstName("Anawat");
		user.setSurName("Jarusiripot");
		user.setRole(Role.Admin);
		user.setSalary(55000.00);
		userService.save(user,"SUPER");
		
	}

}
