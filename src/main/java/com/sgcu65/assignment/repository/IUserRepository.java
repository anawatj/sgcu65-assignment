package com.sgcu65.assignment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sgcu65.assignment.domain.Role;
import com.sgcu65.assignment.domain.User;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
	
	Optional<User> findByEmail(String email);
	
	@Query(" SELECT user FROM User user "+
		   " WHERE (user.firstName LIKE :firstName OR :firstName='') AND "+
			 " (user.surName LIKE :surName OR :surName='') AND "+
		     " (user.role=:role OR :role is null) AND "+
			 " (user.salary=:salary OR :salary is null) ")
   List<User> findAll(String firstName,String surName,Role role,Double salary);
}
