package com.huari.client;

import com.huari.client.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

public class WelcomeActivity extends Activity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start();
                } else {

                }
        }
    }

    // SharedPreferences share;
    // SharedPreferences.Editor editor;
    // int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SysApplication.getInstance().addActivity(this);
        if (ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            start();
        }
        // share=getSharedPreferences("count", MODE_PRIVATE);
        // editor=share.edit();
        // count=share.getInt("count", 0);

    }

    private void start() {
        Handler handler = new Handler();
        handler.postDelayed(new Loading(), 2000);
    }

    class Loading extends Thread {
        public void run() {
            // if(count==0)
            // {
            // startActivity(new
            // Intent(WelcomeActivity.this,GuideActivity.class));
            // }
            // else
            // {
            //startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            startActivity(new Intent(WelcomeActivity.this, Login2Activity.class));
            finish();
            // }
            // editor.putInt("count", 1);
            // editor.commit();
            // WelcomeActivity.this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            SysApplication.getInstance().exit();
        }
        return super.onKeyDown(keyCode, event);
    }

}
