package com.lambton.madt.rockpaperscissors.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lambton.madt.rockpaperscissors.utils.IAppConfig;

import timber.log.Timber;


/**
 * Created by chitrang.
 */

public class PreferenceHelper {

	private static PreferenceHelper sInstance;
	private static SharedPreferences sSharedPreferences;
	private static SharedPreferences.Editor sEditor;

	private PreferenceHelper() {
	}

	public static PreferenceHelper getInstance(Context context) {
		if (sInstance == null) {
			synchronized (PreferenceHelper.class) {
				if (sInstance == null) {
					sInstance = new PreferenceHelper();
					sSharedPreferences = context.getSharedPreferences(IAppConfig.PREFERENCE_NAME,
							Activity.MODE_PRIVATE);
					sEditor = sSharedPreferences.edit();
				}
			}
		}
		return sInstance;
	}

	public void setString(String key, String value) {
		sEditor.putString(key, value);
		sEditor.commit();
	}

	public String getString(String key) {
		return sSharedPreferences.getString(key, null);
	}

	public void setInt(String key, int value) {
		sEditor.putInt(key, value);
		sEditor.commit();
	}

	public int getInt(String key) {
		return sSharedPreferences.getInt(key, 0);
	}

	public void setBoolean(String key, boolean value) {
		sEditor.putBoolean(key, value);
		sEditor.commit();
	}

	public boolean getBoolean(String key) {
		boolean bValue = sSharedPreferences.getBoolean(key, false);
		Timber.d("key = " + key + ", bValue = " + bValue);
		return bValue;
	}

	public void clearAll() {
		sEditor.clear();
		sEditor.commit();
	}
}
