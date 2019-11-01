package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.huari.adapter.DzPagerAdapter;
import com.huari.ui.CirclePainView;
import com.huari.ui.LinView;
import com.huari.ui.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class DzActivity extends AppCompatActivity {
    CirclePainView dlCircleView;
    CirclePainView voltmeterView;
    LinView temperatureView;
    LinView humidityView;
    SwitchButton sbCustom0;
    SwitchButton sbCustom1;
    SwitchButton sbCustom2;
    SwitchButton sbCustom3;
    ImageView back;
    boolean b0 = false;
    boolean b1 = false;
    boolean b2 = false;
    boolean b3 = false;

    int ampereValue, voltmeterValue, temperature, humidity;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            temperature = (int) msg.obj;
            updataView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dz);
        initView();
        data();
    }

    private void data() {
        Runnable runnable = () -> {
            while (true){
                int m = (int) (-60+(Math.random()*140));
                Message message = Message.obtain();
                message.obj = m;
                handler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void initView() {
        dlCircleView = findViewById(R.id.ampere);
        voltmeterView = findViewById(R.id.voltmeter);
        temperatureView = findViewById(R.id.temperature);
        humidityView = findViewById(R.id.humidity);
        sbCustom0 = findViewById(R.id.sb_custom0);
        sbCustom1 = findViewById(R.id.sb_custom1);
        sbCustom2 = findViewById(R.id.sb_custom2);
        sbCustom3 = findViewById(R.id.sb_custom3);
        back = findViewById(R.id.back);

        dlCircleView.setSystemUiVisibility(View.INVISIBLE);
        back.setOnClickListener(v -> finish());

        sbCustom0.setOnClickListener(v -> {
            b0 = !b0;
            Log.d("xioa", String.valueOf(b0));
        });
    }

    public void updataView() {
        if (dlCircleView.getShowValue() != ampereValue) {
            dlCircleView.setShowValue(ampereValue);
        }
        if (voltmeterView.getShowValue() != voltmeterValue) {
            voltmeterView.setShowValue(voltmeterValue);
        }
        if (temperatureView.getValue() != temperature) {
            temperatureView.setValue(temperature);
        }
        if (humidityView.getValue() != humidity) {
            humidityView.setValue(humidity);
        }
    }
}
