package com.lambton.madt.rockpaperscissors.models;

import android.util.Log;

public class Result
{
	public final String userId;
	public final int option;

	private int scores;

	private static final String symbols = "RPSLV";

	public Result(String userId, String option)
    {
		this.userId = userId;
		this.option = symbols.indexOf(option.charAt(0));

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
}
