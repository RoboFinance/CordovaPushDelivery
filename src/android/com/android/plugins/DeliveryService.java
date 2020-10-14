package com.android.plugins;

import com.google.firebase.messaging.RemoteMessage;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.app.NotificationManager;
import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import %PACKAGE%.BuildConfig;

import com.adobe.phonegap.push.FCMService;

@SuppressLint("NewApi")
public class DeliveryService extends FCMService {
  private static final String CONTEXT = "PushDeliveryPlugin";

  @Override
  public void onMessageReceived(RemoteMessage message) {
    Log.d(CONTEXT, "onMessageReceived");
    if (BuildConfig.DEBUG) {
      Log.d(CONTEXT, "trust all in debug mode");
      DeliveryService.initTrustManager();
    }
    super.onMessageReceived(message);

    String key = "delivery_url";

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    boolean notificationsEnabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.N || notificationManager.areNotificationsEnabled();
    if (message.getData().containsKey(key) && notificationsEnabled) {
      Log.d(CONTEXT, "Notification has `delivery_url` and notifications are enabled");
      OutputStream out = null;
      try {
        URL url = new URL(message.getData().get(key));
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        out = new BufferedOutputStream(urlConnection.getOutputStream());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.flush();
        writer.close();
        out.close();
        urlConnection.connect();
        Log.d(CONTEXT, urlConnection.getResponseMessage());
      } catch (Exception e) {
        Log.e(CONTEXT, e.getMessage());
      }
    }
  }

  private static void initTrustManager() {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }
    } };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      Log.e(CONTEXT, e.getMessage());
    }
  }
}