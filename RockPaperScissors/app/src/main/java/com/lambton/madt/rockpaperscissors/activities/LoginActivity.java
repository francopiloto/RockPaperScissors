package com.lambton.madt.rockpaperscissors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.lambton.madt.rockpaperscissors.R;
import com.lambton.madt.rockpaperscissors.models.User;
import com.lambton.madt.rockpaperscissors.utils.IConstants;
import com.lambton.madt.rockpaperscissors.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {
	@BindView(R.id.etx_userid)
	EditText etxUserid;

	public static void startLoginActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.btn_signin)
	public void onClickGoogleSignIn() {
		String userId = etxUserid.getText().toString();
		if (Utils.isNullOrEmpty(userId)) {
			Toast.makeText(LoginActivity.this, "Please enter usr id", Toast.LENGTH_SHORT).show();
		} else {
			checkOnServer(userId);
		}
	}

	private void checkOnServer(final String userId) {
		fbReference.child(IConstants.Firebase.USERS)
				.orderByChild(IConstants.Firebase.USER_ID)
				.equalTo(userId)
				.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						Timber.d("dataSnapshot = " + dataSnapshot.getValue());
						if (dataSnapshot.exists()) {
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								snapshot.getRef().child(IConstants.Firebase.STATUS).setValue(IConstants.Status.ONLINE);
							}
						} else {
							fbReference.child(IConstants.Firebase.USERS)
									.push()
									.setValue(new User(userId, IConstants.Status.ONLINE));
						}
						mPreferenceHelper.setString(IConstants.Preference.USER_ID, userId);
						MapActivity.startMapActivity(LoginActivity.this);
						finish();
					}

					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
						Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});
	}
}
