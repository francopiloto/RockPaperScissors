package com.lambton.madt.rockpaperscissors.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

	protected FirebaseDatabase fbDatabase;
	protected DatabaseReference fbReference;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Access a Cloud Firestore instance from your Activity
		// Write a message to the database
		fbDatabase = FirebaseDatabase.getInstance();
		fbReference = fbDatabase.getReference("message");
	}
}
