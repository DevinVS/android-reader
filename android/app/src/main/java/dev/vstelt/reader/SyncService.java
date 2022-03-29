package dev.vstelt.reader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class SyncService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Reader", "Starting Sync Service");

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopSelf(startId);
                context.unregisterReceiver(this);
            }
        }, new IntentFilter("ARTICLES_UPDATED"));

        Reader.sync(this);
        return Service.START_STICKY;
    }
}
