package com.lambton.madt.rockpaperscissors.models;

/**
 * Created by macstudent on 2018-04-10.
 */

public class User {

	String userId;
	String status;
	double latitude;
	double longitude;


	public User() {
	}

	public User(String userId, String status) {
		this.userId = userId;
		this.status = status;
	}

	public User(String userId, String status, double latitude, double longitude) {
		this.userId = userId;
		this.status = status;
		this.latitude = latitude;
		this.longitude = longitude;
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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
