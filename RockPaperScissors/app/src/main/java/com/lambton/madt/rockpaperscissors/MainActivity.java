package com.lambton.madt.rockpaperscissors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    private ShakeDetector shakeDetector;

/* --------------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shakeDetector = new ShakeDetector(this, new ShakeDetector.ShakeListener()
        {
            @Override
            public void onShake(int count) {
                Log.d("SHAKE", "shake: " + count);
            }
        });
    }

/* --------------------------------------------------------------------------------------------- */

    @Override
    public void onResume()
    {
        super.onResume();
        shakeDetector.setEnabled(true);
    }

/* --------------------------------------------------------------------------------------------- */

    @Override
    public void onPause()
    {
        shakeDetector.setEnabled(false);
        super.onPause();
    }

/* --------------------------------------------------------------------------------------------- */

    public void onClick(View view) {
        GameActions.vibrate(this);
    }

/* --------------------------------------------------------------------------------------------- */

}
