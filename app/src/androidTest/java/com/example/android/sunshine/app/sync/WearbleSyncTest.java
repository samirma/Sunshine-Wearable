package com.example.android.sunshine.app.sync;

import android.content.ContentValues;
import android.test.AndroidTestCase;

import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MAX_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_MIN_TEMP;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_SHORT_DESC;
import static com.example.android.sunshine.app.data.WeatherContract.WeatherEntry.COLUMN_WEATHER_ID;

/**
 * Created by samir on 12/5/16.
 */
public class WearbleSyncTest extends AndroidTestCase {


    public void testUpdateWearble() throws Exception {

        final WearbleSync wearbleSync = new WearbleSync(mContext);

        final ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MAX_TEMP, 1.0d);
        contentValues.put(COLUMN_MIN_TEMP, 2.0d);
        contentValues.put(COLUMN_SHORT_DESC, "desc");
        contentValues.put(COLUMN_WEATHER_ID, "1");
        wearbleSync.updateWearble(contentValues, getContext());

        Thread.sleep(2000);


    }


}