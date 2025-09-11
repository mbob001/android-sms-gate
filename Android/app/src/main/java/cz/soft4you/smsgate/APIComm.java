package cz.soft4you.smsgate;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class APIComm {
    private static final String TAG = "APIComm";
    private static String BASE_URL = "https://192.168.7.104:7279/SMS/";

    private static void init() {
        //Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveReceivedSMS(String sender, String message) {
        HttpURLConnection urlConn;

        init();

        try {
            URL url = new URL(BASE_URL + "SaveReceivedSms");
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(10000);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConn.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", sender);
            jsonObject.put("message", message);

            // Write the data to the output stream
            try (OutputStream os = urlConn.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.d(TAG, "Response: " + response);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error checking URL", e);
        }
    }

    public static List<SmsMessage> getSmsToSend() {
        URLConnection urlConn;
        BufferedReader bufferedReader;
        List<SmsMessage> result = new ArrayList<>();

        init();

        try {
            URL url = new URL(BASE_URL + "GetSmsToSend");
            urlConn = url.openConnection();
            urlConn.setConnectTimeout(10000);
            urlConn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            String jsonString = stringBuffer.toString();
            Log.d(TAG, "JSON String: " + jsonString);

            List<Integer> ids = new ArrayList<>();
            try {
                JSONArray items = new JSONArray(jsonString);
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    String phoneNumber = obj.getString("phone");
                    String message = obj.getString("message");
                    result.add(new SmsMessage(phoneNumber, message));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking URL", e);
        }

        return result;
    }
}
