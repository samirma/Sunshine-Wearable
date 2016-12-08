package com.example.android.sunshine.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class WeatherDetail {

    public static final String COLUMN_WEATHER_ID = "weather_id";
    public static final String IMG = "IMG";

    // Short description and long description of the weather, as provided by API.
    // e.g "clear" vs "sky is clear".
    public static final String COLUMN_SHORT_DESC = "short_desc";

    // Min and max temperatures for the day (stored as floats)
    public static final String COLUMN_MIN_TEMP = "min";
    public static final String COLUMN_MAX_TEMP = "max";
    public static final long TIMEOUT_MS = 1000l;
    public final Asset asset;
    public final String high;
    public final String low;
    public final String desc;
    private final GoogleApiClient mGoogleApiClient;
    public final Bitmap bitmap;

    public WeatherDetail(DataMap config, GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;

        high = config.getString(COLUMN_MAX_TEMP);
        low = config.getString(COLUMN_MIN_TEMP);
        desc = config.getString(COLUMN_SHORT_DESC);
        asset = config.getAsset(IMG);
        bitmap = loadBitmapFromAsset(asset);
    }


    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

}
