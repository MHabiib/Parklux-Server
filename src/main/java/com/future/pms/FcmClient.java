package com.future.pms;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import static com.future.pms.Constants.FIREBASE_API_URL;
import static com.future.pms.Constants.FIREBASE_SERVER_KEY;

@Async
public class FcmClient {
    public void sendPushNotification(String key, String customerName, String parkingZoneName, String levelName) throws JSONException {
        JSONObject msg = new JSONObject();

        msg.put("customerName", customerName);
        msg.put("parkingZoneName", parkingZoneName);
        msg.put("levelName", levelName);
        callToFcmServer(msg, key);
    }

    private void callToFcmServer(JSONObject message, String receiverFcmKey) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "key=" + FIREBASE_SERVER_KEY);
        httpHeaders.set("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        json.put("data", message);
        json.put("to", receiverFcmKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(json.toString(), httpHeaders);
        restTemplate.postForObject(FIREBASE_API_URL, httpEntity, String.class);
    }

    public void sendPushNotificationCheckoutBooking(String key, String parkingZoneName,
        String totalPrice) throws JSONException {
        JSONObject msg = new JSONObject();

        msg.put("parkingZoneName", parkingZoneName);
        msg.put("totalPrice", totalPrice);
        callToFcmServer(msg, key);
    }
}
