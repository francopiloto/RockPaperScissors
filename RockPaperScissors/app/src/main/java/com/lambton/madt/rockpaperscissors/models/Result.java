package com.lambton.madt.rockpaperscissors.models;

public class Result {
	String userId;
	String option;
	private int result;

	public Result() {
	}

	public Result(String userId, String option) {
		this.userId = userId;
		this.option = option;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getResult() {
		return result;
	}
}
