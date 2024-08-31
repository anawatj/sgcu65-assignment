package com.sgcu65.assignment.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractDomain<E> {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private E id;

	public E getId() {
		return id;
	}

	public void setId(E id) {
		this.id = id;
	}
	
}
