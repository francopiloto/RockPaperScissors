package com.lambton.madt.rockpaperscissors;

import android.app.Application;

import timber.log.Timber;

public class RPSApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
	}
}
