package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.adapter.ResultAdapter;
import com.lambton.madt.rockpaperscissors.models.Result;
import com.lambton.madt.rockpaperscissors.proximity.GameActions;
import com.lambton.madt.rockpaperscissors.proximity.ShakeDetector;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class GamePlayActivity extends BaseActivity {
	private static final String GAME_ID = "GAME_ID";
	private static final String BOARD_USER_COUNT = "BOARD_USER_COUNT";
	private static final String GAME_KEY = "GAME_KEY";

	@BindView(R.id.imageView)
	ImageView image;

	@BindView(R.id.rvResult)
	RecyclerView rvResult;

	private ShakeDetector shakeDetector;
	private String gameId;
	private int boardUserCount;
	private String gameKey;
	private ArrayList<Result> resultArrayList;
	private ResultAdapter resultAdapter;
	private boolean isFirstTime = true;

	public static void startGamePlayActivity(Context context, String gameId, int boardUserCount, String gameKey) {
		if (!Utils.isNullOrEmpty(gameId)) {
			Intent intent = new Intent(context, GamePlayActivity.class);
			intent.putExtra(GAME_ID, gameId);

			intent.putExtra(BOARD_USER_COUNT, boardUserCount);
			intent.putExtra(GAME_KEY, gameKey);

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
		boardUserCount = getIntent().getIntExtra(BOARD_USER_COUNT, 0);
		gameKey = getIntent().getStringExtra(GAME_KEY);

		if (Utils.isNullOrEmpty(gameId)) {
			finish();
		}

		setTitle("Game Id : " + gameId);

		resultArrayList = new ArrayList<>();
		resultAdapter = new ResultAdapter(GamePlayActivity.this, resultArrayList);
		LinearLayoutManager llm = new LinearLayoutManager(GamePlayActivity.this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		rvResult.setLayoutManager(llm);
		rvResult.setAdapter(resultAdapter);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvResult.getContext(),
				llm.getOrientation());
		rvResult.addItemDecoration(dividerItemDecoration);

		clearUI();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener() {
			@Override
			public void onShake(int count) {
				Timber.d("shake: " + count);

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

	private void clearUI() {
		image.setBackgroundResource(R.drawable.none);
		image.setImageResource(R.drawable.none);
	}

	/* --------------------------------------------------------------------------------------------- */

	@OnClick(R.id.textView1)
	public void playGame() {
		submitUserOption(Utils.randomRPS());
	}

	// Option - R, P or S
	private void submitUserOption(final String option) {
		if (isFirstTime) {
			isFirstTime = false;
			switch (option) {
				case "R":
					image.setImageResource(R.drawable.rock);
					break;
				case "P":
					image.setImageResource(R.drawable.paper);
					break;
				case "S":
					image.setImageResource(R.drawable.scissors);
					break;
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
											.setValue(option);
								}
							}
						}

						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
							Toast.makeText(GamePlayActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			Toast.makeText(GamePlayActivity.this, "You can play once only.", Toast.LENGTH_SHORT).show();
		}
	}

	private void gamePlayListener() {
		Timber.i("gamePlayListener");

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
								if (!gameSnapshot.getKey().equals(IConstants.Firebase.GAME_ID) &&
										!gameSnapshot.getKey().equals(IConstants.Firebase.BOARD_USER_COUNT)) {
									Result result = new Result(gameSnapshot.getKey(), (String) gameSnapshot.getValue());
									if (result != null) {
										resultArrayList.add(result);
									}
								}
							}
						}
						resultAdapter.notifyDataSetChanged();
						Timber.d("resultArrayList.size() = " + resultArrayList.size());
						if (resultArrayList.size() >= boardUserCount) {
							showResults();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});


//		fbReference.child(IConstants.Firebase.GAMES).child(GAME_KEY).addChildEventListener(new ChildEventListener() {
//			public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//				onChildChanged(dataSnapshot, s);
//			}
//
//			public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//				Timber.i("value = " + dataSnapshot.getValue());
//				if (!dataSnapshot.getKey().equals(IConstants.Firebase.GAME_ID)) {
//					resultArrayList.add(new Result(dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
//
//					if (resultArrayList.size() >= BOARD_USER_COUNT) {
//						showResults();
//					}
//				}
//			}
//
//			public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//			}
//
//			public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//			}
//
//			public void onCancelled(@NonNull DatabaseError databaseError) {
//			}
//		});
	}

	/* --------------------------------------------------------------------------------------------- */

	private void showResults() {
		Timber.i("calculate result");
		String userName = mPreferenceHelper.getString(IConstants.Preference.USER_ID);
		List<Result> winners = GameActions.computeResult(resultArrayList);
		for (Result original : resultArrayList) {
			for (Result winner : winners) {
				if (original.userId.equals(winner.userId)) {
					if (winners.size() == 1) {
						original.setStatus("W");
					} else {
						original.setStatus("T");
					}
				}
			}
		}
		resultAdapter.notifyDataSetChanged();
//		resultArrayList.clear();

		for (Result r : winners) {
			if (r.userId.equals(userName)) {
				if (winners.size() == 1) {
					image.setBackgroundResource(R.drawable.win);
				} else {
					image.setBackgroundResource(R.drawable.tie);
				}

				return;
			}
		}

		image.setBackgroundResource(R.drawable.lose);
	}

	/* --------------------------------------------------------------------------------------------- */


}
