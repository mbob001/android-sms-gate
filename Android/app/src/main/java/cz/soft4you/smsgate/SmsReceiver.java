package cz.soft4you.smsgate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.w(TAG, "Received a null intent.");
            return;
        }

        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage message = getIncomingMessage(pdu, intent);

                        if (message != null) {
                            String sender = message.getOriginatingAddress();
                            String messageBody = message.getMessageBody();

                            Log.d(TAG, "Received SMS from: " + sender + ", Message: " + messageBody);

                            Intent intentB = new Intent("SEND_RECEIVE_SMS");
                            intentB.putExtra("key", "receive");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intentB);

                            executor.execute(() -> {
                                APIComm.saveReceivedSMS(sender,messageBody);
                            });
                        }
                    }
                }
            }
        }
    }

    private SmsMessage getIncomingMessage(Object pdu, Intent intent) {
        SmsMessage currentMessage;
        byte[] pdu_bytes = (byte[]) pdu;

        String format = intent.getStringExtra("format");
        currentMessage = SmsMessage.createFromPdu(pdu_bytes, format);
        return currentMessage;
    }
}
