package com.lambton.madt.rockpaperscissors.proximity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.lambton.madt.rockpaperscissors.models.Result;

import java.util.ArrayList;
import java.util.List;

// please do not change the formatting in this class
public class GameActions
{
    public static final int MINIMUM_PLAYERS = 2;

    private static final long[] vibratePattern = {100, 300, 100, 300, 100, 300};

    private static final int[][][] m =
    {
        {{ 0, 0},{-1, 1},{ 1,-1},{ 1,-1},{-1, 1}},
        {{ 1,-1},{ 0, 0},{-1, 1},{-1, 1},{ 1,-1}},
        {{-1, 1},{ 1,-1},{ 0, 0},{ 1,-1},{-1, 1}},
        {{-1, 1},{ 1,-1},{-1, 1},{ 0, 0},{ 1,-1}},
        {{ 1,-1},{-1, 1},{ 1,-1},{-1, 1},{ 0, 0}}
    };

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

    public static void shakeVibration(Activity activity)
    {
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }

/* --------------------------------------------------------------------------------------------- */

    public static List<Result> computeResult(List<Result> resultArrayList)
    {
        // compute scores for all players
        for (Result r1 : resultArrayList)
        {
            r1.clear();

            for (Result r2 : resultArrayList)
            {
                if (r1.userId.equals(r2.userId)) {
                    continue;
                }

                r1.addScores(computeScores(r1,r2));
            }
        }

        // find winner
        List<Result> winners = new ArrayList<>();

        for (Result r : resultArrayList)
        {
            if (winners.isEmpty()) {
                winners.add(r);
            }
            else if (r.getScores() > winners.get(0).getScores())
            {
                winners.clear();
                winners.add(r);
            }
            else if (r.getScores() == winners.get(0).getScores()) {
                winners.add(r);
            }
        }

        return winners;
    }

/* --------------------------------------------------------------------------------------------- */

    private static int computeScores(Result r1, Result r2)
    {
        int idx1 = m[r1.option][r2.option][0];
        int idx2 = m[r1.option][r2.option][1];

        return idx1 > idx2 ? 3 : (idx1 == idx2 ? 1 : 0);
    }

/* --------------------------------------------------------------------------------------------- */

}
