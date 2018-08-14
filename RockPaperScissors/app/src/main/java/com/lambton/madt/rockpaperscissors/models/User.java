package com.lambton.madt.rockpaperscissors.models;

/**
 * Created by macstudent on 2018-04-10.
 */

public class User {

	String userId;
	String status;

	public User() {
	}

	public User(String userId, String status) {
		this.userId = userId;
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
