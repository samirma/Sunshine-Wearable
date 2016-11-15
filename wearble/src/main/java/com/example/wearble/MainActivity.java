package com.example.wearble;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.wearable.view.WatchViewStub;
import android.text.format.DateUtils;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        final String text = DateUtils.formatDateTime(this, new Date().getTime(), SimpleDateFormat.YEAR_FIELD);
        mTextView.setText(text);


    }
}
