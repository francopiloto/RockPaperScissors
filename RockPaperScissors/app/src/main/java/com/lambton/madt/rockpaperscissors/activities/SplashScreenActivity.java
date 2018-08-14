package com.lambton.madt.rockpaperscissors.activities;

import android.os.Bundle;
import android.os.Handler;

import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

public class SplashScreenActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGTH = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				openNextScreen();
			}
		}, SPLASH_DISPLAY_LENGTH);
	}

	private void openNextScreen() {
		if (Utils.isNullOrEmpty(mPreferenceHelper.getString(IConstants.Preference.USER_ID))) {
			LoginActivity.startLoginActivity(SplashScreenActivity.this);
		} else {
			MapActivity.startMapActivity(SplashScreenActivity.this);
		}
		finish();
	}
}
