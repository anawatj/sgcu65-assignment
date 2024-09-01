package com.sgcu65.assignment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sgcu65.assignment.domain.Team;
@Repository
public interface ITeamRepository extends JpaRepository<Team, UUID> {

	@Override
	@Query("SELECT DISTINCT team FROM Team team  "+
		   " JOIN FETCH team.users users "+
		   " JOIN FETCH team.tasks tasks " +
		   " WHERE team.id=:id ")
	Optional<Team> findById(UUID id);
	
	@Query("SELECT team FROM Team team " +
			"WHERE team.name LIKE :name OR :name = '' ")
	List<Team>  findAll(String name);
}
