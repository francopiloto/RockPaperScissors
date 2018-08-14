package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.proximity.GameActions;
import com.lambton.madt.rockpaperscissors.proximity.ShakeDetector;

import timber.log.Timber;

public class GamePlayActivity extends BaseActivity {
	private ShakeDetector shakeDetector;


	public static void startGamePlayActivity(Context context) {
		Intent intent = new Intent(context, GamePlayActivity.class);
		context.startActivity(intent);
	}
	/* --------------------------------------------------------------------------------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener() {
			@Override
			public void onShake(int count) {
				Timber.d("shake: " + count);
			}
		});
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	public void onResume() {
		super.onResume();
		shakeDetector.setEnabled(true);
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	public void onPause() {
		shakeDetector.setEnabled(false);
		super.onPause();
	}

	/* --------------------------------------------------------------------------------------------- */

	public void onClick(View view) {
		GameActions.vibrate(this);
	}

	/* --------------------------------------------------------------------------------------------- */

}
