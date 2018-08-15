package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.models.Result;
import com.lambton.madt.rockpaperscissors.proximity.ShakeDetector;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class GamePlayActivity extends BaseActivity {
	private static final String GAME_ID = "GAME_ID";
	@BindView(R.id.txtGameId)
	TextView txtGameId;
	private ShakeDetector shakeDetector;
	private String gameId;
	private ArrayList<Result> resultArrayList;


	public static void startGamePlayActivity(Context context, String gameId) {
		if (!Utils.isNullOrEmpty(gameId)) {
			Intent intent = new Intent(context, GamePlayActivity.class);
			intent.putExtra(GAME_ID, gameId);
			context.startActivity(intent);
		}
	}
	/* --------------------------------------------------------------------------------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);
		ButterKnife.bind(this);
		gameId = getIntent().getStringExtra(GAME_ID);
		if (Utils.isNullOrEmpty(gameId)) {
			finish();
		}
		resultArrayList = new ArrayList<>();
		txtGameId.setText("Game Id : " + gameId);
		shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener() {
			@Override
			public void onShake(int count) {
				Timber.d("shake: " + count);
				if (count >= 3) {
//					submitUserOption(Utils.randomRPS());
				}
			}
		});
		gamePlayListener();
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
//		GameActions.vibrate(this);
		submitUserOption(Utils.randomRPS());
	}

	/* --------------------------------------------------------------------------------------------- */

	// Option - R, P or S
	private void submitUserOption(final String option) {
		fbReference.child(IConstants.Firebase.GAMES)
				.orderByChild(IConstants.Firebase.GAME_ID)
				.equalTo(gameId)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						Timber.d("dataSnapshot = " + dataSnapshot.getValue());
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef()
										.child(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
										.setValue(option);
							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Toast.makeText(GamePlayActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void gamePlayListener() {
		fbReference.child(IConstants.Firebase.GAMES)
				.orderByChild(IConstants.Firebase.GAME_ID)
				.equalTo(gameId)
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						resultArrayList.clear();
						Timber.d("game = " + dataSnapshot.getValue());
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
								Timber.d("key = " + gameSnapshot.getKey() + ", value = " + gameSnapshot.getValue());
								if (!gameSnapshot.getKey().equals(IConstants.Firebase.GAME_ID)) {
									Result result = new Result(gameSnapshot.getKey(), (String) gameSnapshot.getValue());
									if (result != null) {
										resultArrayList.add(result);
									}
								}
							}
						}
						Timber.d("resultArrayList.size() = " + resultArrayList.size());
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}
}
