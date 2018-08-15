package com.lambton.madt.rockpaperscissors.utils;

import java.security.SecureRandom;

public class Utils {
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static final String RPS = "RPS";
	static SecureRandom rnd = new SecureRandom();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static String randomRPS() {
		StringBuilder sb = new StringBuilder(1);
		for (int i = 0; i < 1; i++)
			sb.append(RPS.charAt(rnd.nextInt(RPS.length())));
		return sb.toString();
	}
}
