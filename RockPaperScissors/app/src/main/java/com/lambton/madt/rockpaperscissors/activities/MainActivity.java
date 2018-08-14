package com.lambton.madt.rockpaperscissors.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.GameActions;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.ShakeDetector;

public class MainActivity extends BaseActivity {
	private static final String TAG = "test:";
	private ShakeDetector shakeDetector;

	/* --------------------------------------------------------------------------------------------- */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener() {
			@Override
			public void onShake(int count) {
				Log.d("SHAKE", "shake: " + count);
			}
		});
		// attach listeners to the database

		fbReference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// This method is called once with the initial value and again
				// whenever data at this location is updated.
				String value = dataSnapshot.getValue(String.class);
				Log.d(TAG, "Value is: " + value);
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				Log.w(TAG, "Failed to read value.", error.toException());
			}
		});
	}

	public void chatButtonPressed() {
//		Log.d("JENELLE", "button pressed");
//
//		// UI: Get the message from the chat box
//		String message = "e.getText().toString().trim()";
//		if (message.isEmpty()) {
//			return;
//		}
//
//		// UI: Get the current time +  date
//		Date d = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//		String currentDateTimeString = sdf.format(d);
//
//		// LOGIC: Build a chat message object and save it to Firebase
//		ChatMessage m = new ChatMessage("jenelle", message, currentDateTimeString);
//		root.child("messages").push().setValue(m);

		fbReference.setValue("Hello, World!" + System.currentTimeMillis());
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
		chatButtonPressed();
	}

	/* --------------------------------------------------------------------------------------------- */

}
