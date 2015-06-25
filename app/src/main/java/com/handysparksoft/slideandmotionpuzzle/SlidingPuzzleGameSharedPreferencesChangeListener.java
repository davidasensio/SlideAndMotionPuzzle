package com.handysparksoft.slideandmotionpuzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by davasens on 6/18/2015.
 */
public class SlidingPuzzleGameSharedPreferencesChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context context;

    public SlidingPuzzleGameSharedPreferencesChangeListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(context.getResources().getString(R.string.pref_list_dimension))) {
            //Broadcast event
            String action = context.getResources().getString(R.string.broadcast_action_init);
            Intent intent = new Intent(action);
            context.sendBroadcast(intent);
        }

        if (key.equals(context.getResources().getString(R.string.pref_numbers))) {
            String action = context.getResources().getString(R.string.broadcast_action_numbers);
            Intent intent = new Intent(action);
            context.sendBroadcast(intent);
        }

    }
}
