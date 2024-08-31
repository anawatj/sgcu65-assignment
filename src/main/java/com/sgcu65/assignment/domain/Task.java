package com.sgcu65.assignment.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
@Entity
@Table(name = "tasks")
public class Task extends AbstractDomain<UUID> {
	
	
	@Column(name = "name")
	private String name;
	@Column(name = "content")
	private String content;
	@Column(name = "task_status")
	@Enumerated(EnumType.STRING)
	private TaskStatus taskStatus;
	@Column(name = "deadline")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deadline;
	
	@ManyToMany(cascade = {CascadeType.ALL,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinTable( 
            name = "task_users",
            joinColumns = @JoinColumn(name="task_id")

            
    ) 
	private Set<User> users =new HashSet<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public Set<User> getUsers() {
		
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
}
