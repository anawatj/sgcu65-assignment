package com.sgcu65.assignment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgcu65.assignment.domain.Team;
@Repository
public interface ITeamRepository extends JpaRepository<Team, UUID> {

}
