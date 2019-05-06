package com.rux.firesafe.notifications;

import android.util.Log;
import java.io.IOException;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Rux on 03.05.2019.
 */

public class TokenWatcher extends FirebaseInstanceIdService {

    private final OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private static final String baseUri = "http://192.168.0.105:8082";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        public void onTokenRefresh() {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            sendRegistrationToServer(refreshedToken);
        }

        private void sendRegistrationToServer(String refreshedToken) {
            Log.d("TOKEN ", refreshedToken);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("token", refreshedToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(baseUri + "/tokens")
                    .post(body)
                    .build();

            tryHttpRequest(request);

        }

    private void tryHttpRequest(Request request) {
        try {
            Response response = this.httpClient.newCall(request).execute();
            Log.d("RESPONSE", response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
