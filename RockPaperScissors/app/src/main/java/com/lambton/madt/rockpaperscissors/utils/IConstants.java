package com.lambton.madt.rockpaperscissors.utils;


/**
 * Created by chitrang on 29/01/18.
 */

public interface IConstants {

	interface Preference {
		String USER_ID = "USER_ID";
	}

	interface Firebase {
		String USERS = "users";
		String USER_ID = "userId";
		String STATUS = "status";
		String LATITUDE = "latitude";
		String LONGITUDE = "longitude";

		String GAMES = "games";
		String GAME_ID = "gameId";
		String BOARD_USER_COUNT = "boardUserCount";
	}

	interface Status {
		String ONLINE = "online";
		String OFFLINE = "offline";
	}
}
