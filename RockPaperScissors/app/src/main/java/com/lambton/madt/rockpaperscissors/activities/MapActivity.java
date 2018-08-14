package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.utils.IConstants;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MapActivity extends BaseActivity {
	public static void startMapActivity(Context context) {
		Intent intent = new Intent(context, MapActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		ButterKnife.bind(this);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_map_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Timber.d("Selected Item: " + item.getTitle());
		switch (item.getItemId()) {
			case R.id.item_logout:
				logout();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void logout() {
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild("userId")
				.equalTo(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef().child("status").setValue(IConstants.Status.OFFLINE);
							}

							mPreferenceHelper.clearAll();
							LoginActivity.startLoginActivity(MapActivity.this);
							finish();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

	@OnClick(R.id.btn_game_play)
	public void onClickGamePlay() {
		GamePlayActivity.startGamePlayActivity(MapActivity.this);
	}
}
