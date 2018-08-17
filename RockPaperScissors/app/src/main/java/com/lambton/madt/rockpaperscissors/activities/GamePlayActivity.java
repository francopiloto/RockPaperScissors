package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.models.Result;
import com.lambton.madt.rockpaperscissors.proximity.GameActions;
import com.lambton.madt.rockpaperscissors.proximity.ShakeDetector;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class GamePlayActivity extends BaseActivity {
	private static final String GAME_ID = "GAME_ID";
	@BindView(R.id.txtGameId)
	TextView txtGameId;

    @BindView(R.id.imageView)
    ImageView image;

	private ShakeDetector shakeDetector;
	private String gameId;
    private ArrayList<Result> resultArrayList;
	private int numUsers;
	private String gameKey;

	public static void startGamePlayActivity(Context context, String gameId, int numUsers, String gameKey) {
		if (!Utils.isNullOrEmpty(gameId)) {
			Intent intent = new Intent(context, GamePlayActivity.class);
			intent.putExtra(GAME_ID, gameId);

			intent.putExtra("NumUsers", numUsers);
            intent.putExtra("GameKey", gameKey);

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
		numUsers = getIntent().getIntExtra("NumUsers", 0);
        gameKey = getIntent().getStringExtra("GameKey");

		if (Utils.isNullOrEmpty(gameId)) {
			finish();
		}

        resultArrayList = new ArrayList<>();
		txtGameId.setText("Game Id : " + gameId);

        clearUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener() {
			@Override
			public void onShake(int count) {
				Timber.d("shake: " + count);

                clearUI();
                GameActions.shakeVibration(GamePlayActivity.this);

				if (count == 3) {
					submitUserOption(Utils.randomRPS());
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

	private void clearUI()
    {
        image.setBackgroundResource(R.drawable.none);
        image.setImageResource(R.drawable.none);
    }

/* --------------------------------------------------------------------------------------------- */


	// Option - R, P or S
	private void submitUserOption(final String option) {

	    switch(option)
        {
            case "R": image.setImageResource(R.drawable.rock); break;
            case "P": image.setImageResource(R.drawable.paper); break;
            case "S": image.setImageResource(R.drawable.scissors); break;
        }

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
										.setValue(option + System.currentTimeMillis());
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
        /*fbReference.child(IConstants.Firebase.GAMES)
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
                */

        fbReference.child(IConstants.Firebase.GAMES).child(gameKey).addChildEventListener(new ChildEventListener()
        {
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                onChildChanged(dataSnapshot,s);
            }

            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if (!dataSnapshot.getKey().equals(IConstants.Firebase.GAME_ID))
                {
                    resultArrayList.add(new Result(dataSnapshot.getKey(), dataSnapshot.getValue().toString()));

                    if (resultArrayList.size() >= numUsers) {
                        showResults();
                    }
                }
            }

            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

/* --------------------------------------------------------------------------------------------- */

    private void showResults()
    {
        String userName = mPreferenceHelper.getString(IConstants.Preference.USER_ID);
        List<Result> winners = GameActions.computeResult(resultArrayList);
        resultArrayList.clear();

        for (Result r : winners)
        {
            if (r.userId.equals(userName))
            {
                if (winners.size() == 1) {
                    image.setBackgroundResource(R.drawable.win);
                }
                else {
                    image.setBackgroundResource(R.drawable.tie);
                }

                return;
            }
        }

        image.setBackgroundResource(R.drawable.lose);
    }

/* --------------------------------------------------------------------------------------------- */



}
