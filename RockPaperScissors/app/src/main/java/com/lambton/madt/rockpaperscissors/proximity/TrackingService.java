package com.lambton.madt.rockpaperscissors.proximity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class TrackingService {
	private Activity activity;
	private TrackingListener listener;

	private FusedLocationProviderClient client;
	private LocationCallback locationCallback;
	private LocationRequest request;

	/* --------------------------------------------------------------------------------------------- */

	public interface TrackingListener {
		void locationChanged(Location location);
	}

	/* --------------------------------------------------------------------------------------------- */

	public TrackingService(Activity activity, TrackingListener listener) {
		this.activity = activity;
		this.listener = listener;

		locationCallback = new LocationCallback() {
			@Override
			public void onLocationResult(LocationResult locationResult) {
				Location location = locationResult.getLastLocation();

				if (location != null) {
					updateLocation(location);
				}
			}
		};

		client = LocationServices.getFusedLocationProviderClient(activity);

		request = new LocationRequest();
		request.setInterval(10000);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	/* --------------------------------------------------------------------------------------------- */

	public void start() {
		Log.d("LOCATION", "start");

		int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

		if (permission == PackageManager.PERMISSION_GRANTED) {
			client.requestLocationUpdates(request, locationCallback, null);
		}
	}

	/* --------------------------------------------------------------------------------------------- */

	public void stop() {
		Log.d("LOCATION", "stop");
		client.removeLocationUpdates(locationCallback);
	}

	/* --------------------------------------------------------------------------------------------- */

	private void updateLocation(Location location) {
		// TODO update firebase here
		Log.d("LOCATION", location.getLatitude() + ":" + location.getLongitude());

		if (listener != null) {
			listener.locationChanged(location);
		}
	}

	/* --------------------------------------------------------------------------------------------- */

}
