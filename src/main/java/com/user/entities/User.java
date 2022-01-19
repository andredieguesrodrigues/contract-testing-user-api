package com.user.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
@Data
public class User {
	@Id
	private Long id;
	private boolean activeUser;
	private String userName;
	private String initialPassword;
}
