package com.lambton.madt.rockpaperscissors.models;

import android.util.Log;

public class Result {
	public final String userId;
	public final int option;
	public String strOption;

	private int scores;
	private String status = "";


	private static final String symbols = "RPSLV";

	public Result(String userId, String option) {
		this.userId = userId;
		this.strOption = option;
		this.option = symbols.indexOf(option);

		Log.d("RPS", "" + this.option);
	}


	public void addScores(int scores) {
		this.scores += scores;
	}

	public int getScores() {
		return scores;
	}

	public void clear() {
		scores = 0;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
