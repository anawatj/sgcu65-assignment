package com.sgcu65.assignment.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
@Entity
@Table(name = "teams")
public class Team extends AbstractDomain<UUID> {

	@Column(name = "name")
	private String name;
	@ManyToMany(cascade = {CascadeType.ALL,CascadeType.DETACH},fetch = FetchType.LAZY)
	@JoinTable(name = "team_users",
	joinColumns = {@JoinColumn(name="team_id")})
	private Set<User> users =new HashSet<>();
	@ManyToMany(cascade = {CascadeType.ALL,CascadeType.DETACH},fetch = FetchType.LAZY)
	@JoinTable(name = "team_tasks",
	joinColumns = {@JoinColumn(name="team_id")})
	private Set<Task> tasks =new HashSet<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	public Set<Task> getTasks() {
		return tasks;
	}
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	
}
