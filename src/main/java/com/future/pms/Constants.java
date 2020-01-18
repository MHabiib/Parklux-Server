package com.future.pms;

import java.util.ArrayList;
import java.util.Arrays;

public class Constants {

    //Resource server
    public static final String RESOURCE_ID = "resource_id";

    //Authorization Service
    public static final String CLIENT_ID = "pms-client";
    public static final String CLIENT_SECRET = "pms-secret";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String IMPLICIT = "implicit";
    public static final String SCOPE_READ = "read";
    public static final String SCOPE_WRITE = "write";
    public static final String TRUST = "trust";
    public static final Integer ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;
    public static final Integer REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60;
    public static final String MD_ALGORITHM_NOT_AVAILABLE =
        "MD5 algorithm not available.  Fatal (should be in the JDK).";
    public static final String ENCODING_NO_AVAILABLE =
        "UTF-8 encoding not available.  Fatal (should be in the JDK).";

    //Parking zone service
    public static final String SLOT_TAKEN = "T";
    public static final Character SLOT_TAKEN_CHAR = 'T';
    public static final String MY_SLOT = "V";
    public static final String SLOT_EMPTY = "E";
    public static final String SLOT_DISABLE = "D";
    public static final String SLOT_ROAD = "R";
    public static final String SLOT_NULL = "_";
    public static final String SLOT_READY = "O";
    public static final String SLOT_SCAN_ME = "S";

    public static final String LEVEL_ON_EDIT = "X";
    public static final String LEVEL_AVAILABLE = "A";
    public static final String LEVEL_UNAVAILABLE = "U";
    public static final String LEVEL_TAKE_OUT = "Z";

    public static final String SECTION_ONE = "Section 1";
    public static final String SECTION_TWO = "Section 2";
    public static final String SECTION_THREE = "Section 3";
    public static final String SECTION_FOUR = "Section 4";

    public static final String EDIT_MODE = "EditMode";
    public static final String EXIT_EDIT_MODE = "ExitEditMode";

    public static final String UPLOADED_FOLDER = "../assets/";

    public static final String CUSTOMER = "ROLE_CUSTOMER";
    public static final String CUSTOMER_BANNED = "ROLE_CUSTOMER (BANNED)";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String OPEN_HOUR = "00:00 - 00:00";
    public static final String FILE_LOCATION = "../qr/";
    public static final String SLOT_UPDATED = "Slot Updated";
    public static final String PARKING_ZONE_NOT_FOUND = "Parking zone not found !";

    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";

    public static final String NOT_ACTIVE = "NOT_ACTIVE";
    public static final String ACTIVE = "ACTIVE";

    public static final String ALL = "all";
    public static final String ONGOING = "ongoing";
    public static final String PAST = "past";

    public static final Integer TOTAL_SLOT_IN_ROW = 16;
    public static final Integer TOTAL_SLOT_IN_SECTION = 64;
    public static final Integer TOTAL_SLOT_IN_LEVEL = 4 * 64;

    public static final ArrayList<String> LETTER = new ArrayList<>(Arrays
        .asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"));

    public static final ArrayList<String> SLOTS = new ArrayList<>(Arrays
        .asList("_1", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_2", "_2", "_2", "_2", "_2", "_2",
            "_2", "_2", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_2", "_2", "_2", "_2",
            "_2", "_2", "_2", "_2", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_2", "_2",
            "_2", "_2", "_2", "_2", "_2", "_2", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_1",
            "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_1", "_1", "_1", "_1", "_1", "_1",
            "_1", "_1", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_1", "_1", "_1", "_1",
            "_1", "_1", "_1", "_1", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_1", "_1",
            "_1", "_1", "_1", "_1", "_1", "_1", "_2", "_2", "_2", "_2", "_2", "_2", "_2", "_2",
            "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_1", "_2", "_2", "_2", "_2", "_2", "_2",
            "_2", "_2", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_4", "_4", "_4", "_4",
            "_4", "_4", "_4", "_4", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_4", "_4",
            "_4", "_4", "_4", "_4", "_4", "_4", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_3",
            "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_3", "_3", "_3", "_3", "_3", "_3",
            "_3", "_3", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_3", "_3", "_3", "_3",
            "_3", "_3", "_3", "_3", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_3", "_3",
            "_3", "_3", "_3", "_3", "_3", "_3", "_4", "_4", "_4", "_4", "_4", "_4", "_4", "_4",
            "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_4", "_4", "_4", "_4", "_4", "_4",
            "_4", "_4", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_3", "_4", "_4", "_4", "_4",
            "_4", "_4", "_4", "_4"));
}
