package cz.soft4you.smsgate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmsService extends Service {
    private static final String TAG = "SMSService";
    private static final String CHANNEL_ID = "SMSChannel";
    private static final int NOTIFICATION_ID = 1;

    private static final int PERIOD = 10000;

    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("SMS Service")
                .setContentText("Service is running")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_ID, builder.build());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Running periodic task...");

                executor.execute(() -> {
                    checkSmsToSend();
                });

                handler.postDelayed(this, PERIOD);
            }
        };

        handler.postDelayed(runnable, PERIOD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SMS Service";
            String description = "SMS send and receive";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkSmsToSend() {
        List<SmsMessage> items = APIComm.getSmsToSend();

        if (items.isEmpty())
            return;

        for (SmsMessage item : items) {
            sendSms(item);
        }
    }

    private void sendSms(SmsMessage sms) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + sms.Phone));
            intent.putExtra("sms_body", sms.Message);

            Intent sentIntent = new Intent("SMS_SENT");

            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, PendingIntent.FLAG_IMMUTABLE);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sms.Phone, null, sms.Message, sentPI, null);

            Intent intentB = new Intent("SEND_RECEIVE_SMS");
            intentB.putExtra("key", "send");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intentB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
