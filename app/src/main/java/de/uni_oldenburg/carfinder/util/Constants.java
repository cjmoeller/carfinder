package de.uni_oldenburg.carfinder.util;

public class Constants {
    public static final String CREATE_NEW_ENTRY_EXTRA = "CREATE_NEW_ENTRY";
    public static final String NOTIFICATION_CHANNEL_ID = "62387";
    public static final String DATABASE_NAME = "parking-spot-db";
    public static final int NOTIFICATION_ID_FOREGROUND_LOCATION = 15;
    public static final int NOTIFICATION_ID_PARKING_DETECTED = 16;
    public static final String LOG_TAG = "default";
    public static final String ADDRESS_EXTRA = "ADDRESS_EXTRA";
    public static final int DEFAULT_ZOOM = 15;
    public static final int FAILURE_RESULT = 17;
    public static final int SUCCESS_RESULT = 18;
    public static final String ADDRESS_STRING_EXTRA = "ADDRESS_STRING_EXTRA";
    public static final String LOCATION_DATA_EXTRA = "LOCATION_EXTRA";
    public static final String ADDRESS_RECEIVER = "ADDRESS_RECEIVER";
    public static final int REQUEST_IMAGE_CAPTURE = 19;
    public static final String FILEPROVIDER_AUTHORITY = "de.uni_oldenburg.carfinder.fileprovider";
    public static final String EXTRA_PARKING_SPOT = "PARKING_SPOT_EXTRA";
    public static final String MAP_SHARE_URL = "https://www.google.com/maps/search/?api=1&query=";
    public static final String ARGUMENT_SPOT_LIST = "arg-spots";
    public static final int ALARM_REQUEST_CODE = 21;
    public static final int PARKING_METER_PREFERENCE_ENTRIES = 6;
    public static final String EXTRA_LOCATION_MODE = "LOCATION_MODE_EXTRA";
    public static final int LOCATION_MODE_NORMAL = 0; //Normaler Modus der Hintergrundpositionsbestimmung: Benachrichtigung schicken.
    public static final int LOCATION_MODE_ENHANCED = 1; //Erweiterter Modus: Konstantes aufzeichnen der Position
    public static final int LOCATION_MODE_PERSIST_DIRECTLY = 2; //Keine Benachrichtigung schicken, Position direkt speichern (für Wearable extension)
    public static final int REQUEST_HISTORY_STATE = 22;
    public static final String ALARM_EXPIRED_INTENT = "ALARM_EXTRA";
}
