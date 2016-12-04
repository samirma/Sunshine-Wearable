package com.example.android.sunshine.app.sync;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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

    private static String oldData = "";

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
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    protected void updateWearble(ContentValues contentValues) {

        double high = contentValues.getAsDouble(COLUMN_MAX_TEMP);
        double low = contentValues.getAsDouble(COLUMN_MIN_TEMP);
        String desc = contentValues.getAsString(COLUMN_SHORT_DESC);
        int weatherId = new Integer(contentValues.getAsString(COLUMN_WEATHER_ID));

        final String dataString = String.format("%s-%s-%s-%s", weatherId, desc, low, high);
        if (!oldData.equals(dataString)) {
            oldData = dataString;
            final Bitmap bitmap = SunshineSyncAdapter.getBitmap(context, weatherId, context.getResources());
            Asset asset = createAssetFromBitmap(bitmap);

            onStart();

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create(WEATHER);
            final DataMap dataMap = putDataMapReq.getDataMap();

            dataMap.putDouble(COLUMN_MAX_TEMP, high);
            dataMap.putDouble(COLUMN_MIN_TEMP, low);
            dataMap.putString(COLUMN_SHORT_DESC, desc);
            dataMap.putAsset(IMG, asset);

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

            onStop();

        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
