package com.lambton.madt.rockpaperscissors;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.data.PreferenceHelper;
import com.lambton.madt.rockpaperscissors.utils.IConstants;

import timber.log.Timber;

public class RPSApplication extends Application implements LifecycleObserver {

	protected FirebaseDatabase fbDatabase;
	protected DatabaseReference fbReference;
	protected PreferenceHelper mPreferenceHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
		fbDatabase = FirebaseDatabase.getInstance();
		fbReference = fbDatabase.getReference();
		mPreferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	public void onMoveToForeground() {
		Timber.d("App: onMoveToForeground");
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild(IConstants.Firebase.USER_ID)
				.equalTo(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef().child(IConstants.Firebase.STATUS).setValue(IConstants.Status.ONLINE);
							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	public void onMoveToBackground() {
		Timber.d("App: onMoveToBackground");
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild(IConstants.Firebase.USER_ID)
				.equalTo(mPreferenceHelper.getString(IConstants.Preference.USER_ID))
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef().child(IConstants.Firebase.STATUS).setValue(IConstants.Status.OFFLINE);
							}
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {

					}
				});
	}

}
