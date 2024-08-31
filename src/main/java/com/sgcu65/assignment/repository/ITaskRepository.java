package com.sgcu65.assignment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sgcu65.assignment.domain.Task;

@Repository
public interface ITaskRepository extends JpaRepository<Task,UUID> {
	@Override
	@Query("SELECT DISTINCT task FROM Task task "+
		   " JOIN FETCH task.users users "+
		   " WHERE task.id=:id ")
	Optional<Task> findById(UUID id);
}
