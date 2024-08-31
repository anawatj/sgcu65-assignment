package com.sgcu65.assignment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgcu65.assignment.domain.User;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
	
	Optional<User> findByEmail(String email);
}
