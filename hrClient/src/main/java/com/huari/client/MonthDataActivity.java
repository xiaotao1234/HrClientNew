package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.huari.ui.CalendarLayout;
import com.huari.ui.CalendarView;

public class MonthDataActivity extends AppCompatActivity {
    CalendarView calendarView;
    CalendarLayout calendarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_data);
        calendarView = findViewById(R.id.calendar);
        calendarLayout = findViewById(R.id.item_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        calendarView.setSystemUiVisibility(View.INVISIBLE);
        calendarLayout.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }
}
