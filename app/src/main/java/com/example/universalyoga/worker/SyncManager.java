package com.example.universalyoga.worker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.NetworkType;


import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.universalyoga.utils.Util;

import java.util.concurrent.TimeUnit;

public class SyncManager {
    public static void startSyncing(Context context) {
        if(Util.checkNetworkConnection(context)) {
            Toast.makeText(context, "Syncing started!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "No internet connection, all sync actions will be suspended...", Toast.LENGTH_LONG).show();
        }

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(SyncWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(syncRequest);
        String TAG = "SyncManager";
        Log.d(TAG, "Started syncing!");
    }
}