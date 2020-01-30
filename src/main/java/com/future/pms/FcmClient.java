package com.future.pms;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

public class FcmClient {
    private final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final String FIREBASE_SERVER_KEY =
        "AAAAqj5yPX8:APA91bHsms0YAbeQ8p6Mwlk-AJSdMfA_T7GzLik5DtZUyeb8V5Bz_JpCnYsyTOf7PGYFC3ON1LuBL0qN5y4M5i3IwEetyQ8RM6bvb2JiOzGGN2cJJ0AYLutswycprUY3_jiUwyQCjFJW";

    @Async public void sendPushNotification(String key, String customerName, String parkingZoneName,
        String levelName) throws JSONException {
        JSONObject msg = new JSONObject();

        msg.put("customerName", customerName);
        msg.put("parkingZoneName", parkingZoneName);
        msg.put("levelName", levelName);
        System.out.println("\nCalling fcm Server >>>>>>>");
        String response = callToFcmServer(msg, key);
        System.out.println("Got response from fcm Server : " + response + "\n\n");
    }

    private String callToFcmServer(JSONObject message, String receiverFcmKey) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "key=" + FIREBASE_SERVER_KEY);
        httpHeaders.set("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        json.put("data", message);
        json.put("to", receiverFcmKey);

        System.out.println("Sending :" + json.toString());

        HttpEntity<String> httpEntity = new HttpEntity<>(json.toString(), httpHeaders);
        return restTemplate.postForObject(FIREBASE_API_URL, httpEntity, String.class);
    }
}
