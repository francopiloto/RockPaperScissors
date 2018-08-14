package com.lambton.madt.rockpaperscissors.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.proximity.TrackingService;
import com.lambton.madt.rockpaperscissors.utils.IConstants;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
    TrackingService.TrackingListener
{
    
    private GoogleMap map;
    private TrackingService trackingService;
    private Location location;
    private Marker playerMarker;
    
    private static final int PERMISSIONS_REQUEST = 100;
    
/* --------------------------------------------------------------------------------------------- */
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        
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
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                              PERMISSIONS_REQUEST);
        }
    }
    
/* --------------------------------------------------------------------------------------------- */
    
    @Override
    protected void onDestroy()
    {
        trackingService.stop();
        super.onDestroy();
    }
    
/* --------------------------------------------------------------------------------------------- */
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        
        if (location != null) {
            locationChanged(location);
        }
    }
    
/* --------------------------------------------------------------------------------------------- */
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            trackingService.start();
        }
        else
        {
            Toast.makeText(this, "Please enable location services to allow GPS tracking",
                           Toast.LENGTH_SHORT).show();
        }
    }
    
/* --------------------------------------------------------------------------------------------- */
    
    @Override
    public void locationChanged(Location location)
    {
        this.location = location;
        
        if (map == null) {
            return;
        }
        
        LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
        
        if (playerMarker != null) {
            playerMarker.setPosition(latLong);
        }
        else {
            playerMarker = map.addMarker(new MarkerOptions().title("PlayerName").position(latLong));
        }
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, map.getMaxZoomLevel()));
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
