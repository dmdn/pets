package by.ddv.zoo.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import java.util.concurrent.TimeUnit;

import by.ddv.zoo.MainActivity;
import by.ddv.zoo.R;


public class UpdateZooService extends IntentService {

    public static String lastUpdate = null;
    public UpdateZooService() {
        super("UpdateZooService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        lastUpdate = intent.getStringExtra("last_update").toString();

        while (lastUpdate != null){
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (lastUpdate == null){
                break;
            } else {
                showNotification(lastUpdate);
            }
        }

        stopSelf();
    }


    private void showNotification(String date) {

        Intent intent2 = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent2);// Adds the Intent that starts the Activity to the top of the stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_smal)
                .setContentTitle("Update ZOO")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)// Hides the notification after its been selected
                .setContentText("The last update was " + date);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }
}