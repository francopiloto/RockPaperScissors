package com.lambton.madt.rockpaperscissors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener
{
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.0f;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 1500;

    private long shakeTimestamp;
    private int shakeCount;
    private ShakeListener listener;

    private SensorManager sensorManager;
    private Sensor accelerometer;

/* --------------------------------------------------------------------------------------------- */

    public interface ShakeListener {
        void onShake(int count);
    }

/* --------------------------------------------------------------------------------------------- */

    public ShakeDetector(Activity activity, ShakeListener listener)
    {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        this.listener = listener;
    }

/* --------------------------------------------------------------------------------------------- */

    public void setEnabled(boolean enabled)
    {
        if (accelerometer == null) {
            return;
        }

        if (enabled) {
            sensorManager.registerListener(this, accelerometer,	SensorManager.SENSOR_DELAY_UI);
        }
        else {
            sensorManager.unregisterListener(this);
        }
    }

/* --------------------------------------------------------------------------------------------- */

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (listener == null) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce < SHAKE_THRESHOLD_GRAVITY) {
            return;
        }

        final long now = System.currentTimeMillis();

        // ignore shake events too close to each other
        if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
            return;
        }

        // reset the shake count after some time of no shakes
        if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
            shakeCount = 0;
        }

        shakeTimestamp = now;
        shakeCount++;

        listener.onShake(shakeCount);
    }

/* --------------------------------------------------------------------------------------------- */

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

/* --------------------------------------------------------------------------------------------- */

}
