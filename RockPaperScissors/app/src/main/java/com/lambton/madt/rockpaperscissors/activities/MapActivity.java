package com.lambton.madt.rockpaperscissors.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.models.User;
import com.lambton.madt.rockpaperscissors.proximity.TrackingService;
import com.lambton.madt.rockpaperscissors.utils.IAppConfig;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
		TrackingService.TrackingListener {

	private GoogleMap map;
	private TrackingService trackingService;
	private Location location;
	private Marker playerMarker;

	private static final int PERMISSIONS_REQUEST = 100;

	private ArrayList<User> userArrayList;

	/* --------------------------------------------------------------------------------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		ButterKnife.bind(this);


		setTitle(mPreferenceHelper.getString(IConstants.Preference.USER_ID));

		userArrayList = new ArrayList<>();

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		trackingService = new TrackingService(this, this);

		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			finish();
		}

		int permission = ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION);

		if (permission == PackageManager.PERMISSION_GRANTED) {
			trackingService.start();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST);
		}

//		onLineUserListener();
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	protected void onDestroy() {
		trackingService.stop();
		super.onDestroy();
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		if (map != null) {
			onLineUserListener();
		}

		if (location != null) {
			locationChanged(location);
		}
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			trackingService.start();
		} else {
			Toast.makeText(this, "Please enable location services to allow GPS tracking",
					Toast.LENGTH_SHORT).show();
		}
	}

	/* --------------------------------------------------------------------------------------------- */

	@Override
	public void locationChanged(Location location) {
		this.location = location;

		if (map == null) {
			return;
		}

		LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());

		if (playerMarker != null) {
			playerMarker.setPosition(latLong);
		} else {
			playerMarker = map.addMarker(new MarkerOptions()
					.title(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
					.position(latLong));
		}

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, map.getMaxZoomLevel() - 3));
		updateLocationOnFb(latLong);
	}

	/* --------------------------------------------------------------------------------------------- */

	public static void startMapActivity(Context context) {
		Intent intent = new Intent(context, MapActivity.class);
		context.startActivity(intent);
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
			case R.id.item_start:
				startGame();
				return true;
			case R.id.item_join:
				joinGame();
				return true;
			case R.id.item_logout:
				logout();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void startGame() {
		if (userArrayList.size() >= IAppConfig.MINIMUM_PLAYERS) {
			final EditText taskEditText = new EditText(this);
			taskEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
			taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle("Create board :")
					.setMessage("Enter number of players(at least 2)")
					.setView(taskEditText)
					.setPositiveButton("Join", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int boardUserCount = Integer.valueOf(taskEditText.getText().toString());
							if (boardUserCount < 2) {
								Toast.makeText(MapActivity.this, "2 or more players required to play the game", Toast.LENGTH_SHORT).show();
							} else if (boardUserCount > userArrayList.size()) {
								Toast.makeText(MapActivity.this, "Board users count must be less then total number of online user", Toast.LENGTH_LONG).show();
							} else {
								String gameId = Utils.randomString(IAppConfig.GAME_ID_LENGTH);
								if (!Utils.isNullOrEmpty(gameId)) {
									createGame(gameId, boardUserCount);
								}
							}
						}
					})
					.setNegativeButton("Cancel", null)
					.create();
			dialog.show();

		} else {
			Toast.makeText(MapActivity.this, "2 or more players required to play the game", Toast.LENGTH_SHORT).show();
		}
	}

	private void joinGame() {
		if (userArrayList.size() >= IAppConfig.MINIMUM_PLAYERS) {
			final EditText taskEditText = new EditText(this);
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle("Join the game")
					.setMessage("Enter valid game id")
					.setView(taskEditText)
					.setPositiveButton("Join", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String gameId = String.valueOf(taskEditText.getText());
							if (!Utils.isNullOrEmpty(gameId)) {
								checkAndJoinGame(gameId);
							}
						}
					})
					.setNegativeButton("Cancel", null)
					.create();
			dialog.show();
		} else {
			Toast.makeText(MapActivity.this, "2 or more players required to play the game", Toast.LENGTH_SHORT).show();
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
						}
						mPreferenceHelper.clearAll();
						LoginActivity.startLoginActivity(MapActivity.this);
						finish();
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

	private void onLineUserListener() {
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild(IConstants.Firebase.STATUS)
				.equalTo(IConstants.Status.ONLINE)
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						userArrayList.clear();
						if (map != null) {
							map.clear();
						}
						Timber.d("online users = " + dataSnapshot.getValue());
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//							snapshot.getRef().child("status").setValue(IConstants.Status.OFFLINE);
							User user = snapshot.getValue(User.class);
							if (user != null && location != null) {

								Location otherUserPoint = new Location("locationA");
								otherUserPoint.setLatitude(user.getLatitude());
								otherUserPoint.setLongitude(user.getLongitude());
								double distance = location.distanceTo(otherUserPoint);
								Timber.d("user.getUserId() = " + user.getUserId() + ", distance = " + distance);
								if (distance < IAppConfig.RANGE) {
									userArrayList.add(user);
								}
							}
						}
						Timber.d("userArrayList.size() = " + userArrayList.size());

						for (User user : userArrayList) {
							LatLng latLong = new LatLng(user.getLatitude(), user.getLongitude());
							if (map != null) {
								map.addMarker(new MarkerOptions()
										.title(user.getUserId())
										.position(latLong));
							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

	private void updateLocationOnFb(final LatLng latLong) {
		Timber.i("updateLocationOnFb");
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild(IConstants.Firebase.USER_ID)
				.equalTo(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef().child(IConstants.Firebase.LATITUDE).setValue(latLong.latitude);
								snapshot.getRef().child(IConstants.Firebase.LONGITUDE).setValue(latLong.longitude);
							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

	private void createGame(final String gameId, final int boardUserCount) {
		DatabaseReference ref = fbReference.child(IConstants.Firebase.GAMES).push();
		ref.child(IConstants.Firebase.GAME_ID).setValue(gameId);
		ref.child(IConstants.Firebase.BOARD_USER_COUNT).setValue(boardUserCount);
		GamePlayActivity.startGamePlayActivity(MapActivity.this, gameId, boardUserCount, ref.getKey());
	}

	private void checkAndJoinGame(final String gameId) {
		fbReference.child(IConstants.Firebase.GAMES)
				.orderByChild(IConstants.Firebase.GAME_ID)
				.equalTo(gameId)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						Timber.d("dataSnapshot = " + dataSnapshot.getValue());
						if (dataSnapshot.exists()) {
							boolean isFirstTime = true;
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								if (isFirstTime) {
									isFirstTime = false;
									int boardUserCount = snapshot.child(IConstants.Firebase.BOARD_USER_COUNT).getValue(Integer.class);
									Timber.d("boardUserCount = " + boardUserCount);
									GamePlayActivity.startGamePlayActivity(MapActivity.this, gameId, boardUserCount, dataSnapshot.getKey());
								}
							}
						} else {
							Toast.makeText(MapActivity.this, "Invalid game id", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Toast.makeText(MapActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}
}
