package com.lambton.madt.rockpaperscissors.proximity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class GameActions
{
    private static final long[] vibratePattern = {100, 300, 100, 300, 100, 300};

/* --------------------------------------------------------------------------------------------- */

    public static void vibrate(Activity activity)
    {
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createWaveform(vibratePattern, -1));
        }
        else {
            v.vibrate(vibratePattern, -1);
        }
    }

/* --------------------------------------------------------------------------------------------- */

}
