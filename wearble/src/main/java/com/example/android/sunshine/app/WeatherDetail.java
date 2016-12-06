package com.example.android.sunshine.app;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;

public class WeatherDetail {

    public static final String COLUMN_WEATHER_ID = "weather_id";
    public static final String IMG = "IMG";

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    public static final String COLUMN_SHORT_DESC = "short_desc";

    // Min and max temperatures for the day (stored as floats)
    public static final String COLUMN_MIN_TEMP = "min";
    public static final String COLUMN_MAX_TEMP = "max";
    private final Asset asset;
    private final double high;
    private final double low;
    private final String desc;

    public WeatherDetail(DataMap config) {
        high = config.getDouble(COLUMN_MAX_TEMP);
        low = config.getDouble(COLUMN_MIN_TEMP);
        desc = config.getString(COLUMN_SHORT_DESC);
        asset = config.getAsset(IMG);
    }
}
