package com.future.pms;

public class Constants {

    //Resource server
    public static final String RESOURCE_ID = "resource_id";

    //Authorization Service
    public static final String CLIEN_ID = "pms-client";
    public static final String CLIENT_SECRET = "pms-secret";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String IMPLICIT = "implicit";
    public static final String SCOPE_READ = "read";
    public static final String SCOPE_WRITE = "write";
    public static final String TRUST = "trust";
    public static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;

    //Parking zone service
    public static final String AVAILABLE = "AVAILABLE";
    public static final String SCAN_ME = "SCAN_ME";
    public static final String DISABLE = "DISABLE";
    public static final String UPLOADED_FOLDER = "../assets/";

    public static final String CUSTOMER = "CUSTOMER";
    public static final String ADMIN = "ADMIN";
    public static final String OPEN_HOUR = "-- : -- / -- : --";
    public static final String FILE_LOCATION = "../tmp/";
    public static final String BOOKED = "BOOKED";
    public static final String SLOT_UPDATED = "Slot Updated";
    public static final String PARKING_ZONE_NOT_FOUND = "Parking zone not found !";

}
