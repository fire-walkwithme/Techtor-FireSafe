package com.rux.firesafe.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Rux on 03.05.2019.
 */

public class TokenWatcher extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceServi";
    private final Gson gson = new Gson();
    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private static final String baseUri = "http://192.168.0.105:8082";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        public void onTokenRefresh() {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            Log.d(TAG, "Refreshed token: " + refreshedToken);
            sendRegistrationToServer(refreshedToken);
        }

        private void sendRegistrationToServer(String refreshedToken) {
            Log.d("TOKEN ", refreshedToken.toString());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", refreshedToken);
            } catch (JSONException e) {

            }

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(baseUri + "/tokens")
                    .post(body)
                    .build();

            Response response = tryHttpRequest(request);

        }

    private Response tryHttpRequest(Request request) {
        try {
            final Response response =
                    this.httpClient.newCall(request).execute();
            Log.d("Response", response.body().string());
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not retrieve specified error");
        }
    }
}


