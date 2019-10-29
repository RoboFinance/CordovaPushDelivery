package com.android.plugins;

import com.google.firebase.messaging.RemoteMessage;
import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.adobe.phonegap.push.FCMService;

@SuppressLint("NewApi")
public class DeliveryService extends FCMService {
  @Override
  public void onMessageReceived(RemoteMessage message) {
    Log.d("DeliveryPlugin", "received");

    super.onMessageReceived(message);

    String key = "delivery_url";

    if (message.getData().containsKey(key)) {
      Log.d("DeliveryPlugin", "containsKey - true");

      OutputStream out = null;

      try {
        URL url = new URL(message.getData().get(key));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        out = new BufferedOutputStream(urlConnection.getOutputStream());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.flush();
        writer.close();
        out.close();

        urlConnection.connect();
      } catch (Exception e) {
        Log.e("DeliveryPlugin", e.getMessage());
      }

    }

  }
}