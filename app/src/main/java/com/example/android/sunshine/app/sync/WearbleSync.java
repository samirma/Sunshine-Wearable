package com.example.android.sunshine.app.sync;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.example.android.sunshine.app.BuildConfig;
import com.example.android.sunshine.app.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_SHORT_DESC;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;

public class WearbleSync {

    public static final String LOG_TAG = WearbleSync.class.getSimpleName();
    public static final String IMG = "IMG";
    public static final String WEATHER = "/weather";
    private final GoogleApiClient mGoogleApiClient;
    private final Context context;

    public static String oldData = "";

    public WearbleSync(final Context context){
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(LOG_TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(LOG_TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(LOG_TAG, "onConnectionFailed: " + result);
                    }
                })
                // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();

        onStart();

    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    protected void updateWearble(ContentValues contentValues, Context context) {

        double high = contentValues.getAsDouble(COLUMN_MAX_TEMP);
        double low = contentValues.getAsDouble(COLUMN_MIN_TEMP);
        String desc = contentValues.getAsString(COLUMN_SHORT_DESC);
        int weatherId = new Integer(contentValues.getAsString(COLUMN_WEATHER_ID));


        PutDataMapRequest request = PutDataMapRequest.create(WEATHER);
        DataMap map = request.getDataMap();

        final Bitmap bitmap = SunshineSyncAdapter.getBitmap(this.context, weatherId, this.context.getResources());

        map.putString(COLUMN_MAX_TEMP, Utility.formatTemperature(context, high));
        map.putString(COLUMN_MIN_TEMP, Utility.formatTemperature(context, low));
        map.putString(COLUMN_SHORT_DESC, desc);
        map.putInt(COLUMN_WEATHER_ID, weatherId);
        final Bitmap bitmap1 = bitmap;
        map.putAsset(IMG, createAssetFromBitmap(bitmap1));

        if (BuildConfig.DEBUG){
            final String dataString = String.format("%s", System.currentTimeMillis());
            map.putString("dataString", dataString);
            request.setUrgent();
        }

        Wearable.DataApi.putDataItem(mGoogleApiClient, request.asPutDataRequest());

    }


    protected void onStart() {
        mGoogleApiClient.connect();
    }


}
