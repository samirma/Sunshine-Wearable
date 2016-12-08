package com.example.android.sunshine.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class WearbleSync {

    public static final String LOG_TAG = WearbleSync.class.getSimpleName();
    public static final String IMG = "IMG";
    public static final String WEATHER = "/weather";
    private final GoogleApiClient mGoogleApiClient;
    private final Context context;

    public static String oldData = "";
    private final WearbleView view;

    public WearbleSync(final Context context, WearbleView wearbleView){
        this.context = context;
        view = wearbleView;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(LOG_TAG, "onConnected: " + connectionHint);
                        Wearable.DataApi.addListener(mGoogleApiClient, onDataChangedListener);
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
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

    }


    public DataApi.DataListener onDataChangedListener = new DataApi.DataListener() {
        @Override

        public void onDataChanged(DataEventBuffer dataEvents) {
            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED &&
                        event.getDataItem().getUri().getPath().equals(WEATHER)) {

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    final DataMap dataMap = dataMapItem.getDataMap();

                    new Thread() {
                        @Override
                        public void run() {
                            final WeatherDetail weatherDetail = new WeatherDetail(dataMap, mGoogleApiClient);
                            view.setWeatherDetail(weatherDetail);
                        }
                    }.start();

                }
            }
        }
    };

    public void disconnect() {
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }
}
