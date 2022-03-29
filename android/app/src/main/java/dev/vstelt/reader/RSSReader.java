package dev.vstelt.reader;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class RSSReader extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("Reader", "Registering Sync Service");
        Intent syncService = new Intent(this, SyncService.class);
        PendingIntent i = PendingIntent.getService(this, 0, syncService, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        am.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_HOUR,
            i
        );

        Log.i("Reader", "Start Sync Service From Application");
        startService(syncService);
    }
}
