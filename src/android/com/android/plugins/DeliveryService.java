package com.android.plugins;

import com.google.firebase.messaging.RemoteMessage;
import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

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
    // Check if channel if enabled
    if (notificationsEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          NotificationManager manager = getSystemService(NotificationManager.class);
          List<NotificationChannel> channels = manager.getNotificationChannels();
          String channelID = DEFAULT_CHANNEL_ID;
          if (channels.size() == 1) {
            channelID = channels.get(0).getId();
          }
          NotificationChannel channel = manager.getNotificationChannel(channelID);

          notificationsEnabled = channel != null && channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
          if (!notificationsEnabled) {
            Log.d(CONTEXT, "Notification channel " + channelID + " is disabled");
          }
    }
    if (message.getData().containsKey(key) && notificationsEnabled) {
      Log.d(CONTEXT, "Notification has `finance.robo.notification_delivery_url` and notifications are enabled");
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
