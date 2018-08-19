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

// please do not change the formatting in this class
public class TrackingService
{
    private static final int REFRESH_INTERVAL_MS = 2000;

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

	public TrackingService(Activity activity, TrackingListener listener)
    {
		this.activity = activity;
		this.listener = listener;

		locationCallback = new LocationCallback()
        {
			@Override
			public void onLocationResult(LocationResult locationResult)
            {
				Location location = locationResult.getLastLocation();

				if (location != null) {
					updateLocation(location);
				}
			}
		};

		client = LocationServices.getFusedLocationProviderClient(activity);

		request = new LocationRequest();
		request.setInterval(REFRESH_INTERVAL_MS);
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

/* --------------------------------------------------------------------------------------------- */

	public void start()
    {
		int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

		if (permission == PackageManager.PERMISSION_GRANTED) {
			client.requestLocationUpdates(request, locationCallback, null);
		}
	}

/* --------------------------------------------------------------------------------------------- */

	public void stop() {
		client.removeLocationUpdates(locationCallback);
	}

/* --------------------------------------------------------------------------------------------- */

	private void updateLocation(Location location)
    {
		if (listener != null) {
			listener.locationChanged(location);
		}
	}

/* --------------------------------------------------------------------------------------------- */

}
