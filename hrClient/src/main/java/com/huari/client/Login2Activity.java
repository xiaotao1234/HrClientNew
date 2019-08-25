package com.huari.client;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.dataentry.GlobalData;
import com.huari.service.ForHideServiceIcon;
import com.huari.service.MainService;
import com.wang.avi.AVLoadingIndicatorView;

public class Login2Activity extends AppCompatActivity {
    FrameLayout frameLayout;
    LinearLayout linearLayout;
    EditText editTextIp;
    EditText editTextPort1;
    EditText editTextPort2;
    TextView loginTextButton;
    TextInputLayout ipEditLayout;
    TextInputLayout port1EditLayout;
    TextInputLayout port2EditLayout;
    public static AVLoadingIndicatorView avLoadingIndicatorView;
    public static Handler handler;
    SharedPreferences preferences;
    SharedPreferences.Editor seditor;
    String ip;
    int port1, port2;
    int saveStationCount;// 单频测向，多线交汇指示出信号源方向时会用到，
    // 表示已经保存了多少个示向度。删除数据不会使其变小，主要用作key的一部分。
    Intent serviceIntent;
    public static int LINKFAILED = 1;
    public static int LINKSUCCESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        initViews();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == LINKFAILED) {
                    Toast.makeText(Login2Activity.this, "连接服务器失败",
                            Toast.LENGTH_SHORT).show();
                    setTitle("未登录");
                    GlobalData.mainTitle = "未登录";
                } else if (msg.what == LINKSUCCESS) {
                    Toast.makeText(Login2Activity.this, "连接服务器成功",
                            Toast.LENGTH_SHORT).show();
                    setTitle("已登录");
                    GlobalData.mainTitle = "已登录";
                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(Login2Activity.this, Main2Activity.class));
                }
            }
        };
    }

    private void initViews() {
                                            startActivity(new Intent(Login2Activity.this, Main2Activity.class));
        frameLayout = findViewById(R.id.contact_edit_frame);
        linearLayout = findViewById(R.id.contact_edit_linearlayout);
        editTextIp = findViewById(R.id.ip_edit);
        editTextPort1 = findViewById(R.id.port1_edit);
        editTextPort2 = findViewById(R.id.port2_edit);
        loginTextButton = findViewById(R.id.main_btn_login);
        avLoadingIndicatorView = findViewById(R.id.login_animtion);
        ipEditLayout = findViewById(R.id.ip_edit_layout);
        port1EditLayout = findViewById(R.id.port1_edit_layout);
        port2EditLayout = findViewById(R.id.port2_edit_layout);
        avLoadingIndicatorView.setVisibility(View.INVISIBLE);
        editTextIp.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editTextPort1.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editTextPort2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        ipEditLayout.setHint("IP: ");
        port1EditLayout.setHint("Port1: ");
        port2EditLayout.setHint("Port2: ");
        loginTextButton.setOnClickListener(v -> {
            editTextIp.setVisibility(View.INVISIBLE);
            editTextPort1.setVisibility(View.INVISIBLE);
            editTextPort2.setVisibility(View.INVISIBLE);
            inputAnimator();
            saveStationCount = preferences.getInt("savecount", -1);
            if (saveStationCount == -1) {
                seditor.putInt("savecount", 0);
            }
            GlobalData.mainIP = editTextIp.getText().toString();
            GlobalData.port1 = Integer.parseInt(editTextPort1.getText().toString());
            GlobalData.port2 = Integer.parseInt(editTextPort2.getText().toString());
            serviceIntent = new Intent();
            serviceIntent.setAction("com.huari.service.mainservice");
            serviceIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
            try {
                ip = editTextIp.getText().toString();
                port1 = Integer.parseInt(editTextPort1
                        .getText().toString());
                port2 = Integer.parseInt(editTextPort2
                        .getText().toString());
                seditor.putInt("port1", port1);
                seditor.putInt("port2", port2);
                seditor.putString("ip", ip);
                GlobalData.mainIP = ip;
                GlobalData.port1 = port1;
                GlobalData.port2 = port2;
                seditor.commit();

                if (GlobalData.toCreatService == false) {
                    new Thread() {
                        public void run() {
                            startService(serviceIntent);
                            MainService.startFunction();
                            GlobalData.toCreatService = true;
                        }
                    }.start();
                }
            } catch (Exception e) {
                GlobalData.mainIP = ip;
                GlobalData.port1 = port1;
                GlobalData.port2 = port2;
            }
        });
        preferences = getSharedPreferences("myclient", MODE_PRIVATE);
        seditor = preferences.edit();
        ip = preferences.getString("ip", "192.168.1.249");
        port1 = preferences.getInt("port1", 5000);
        port2 = preferences.getInt("port2", 5012);
        editTextIp.setText(ip);
        editTextPort1.setText(port1 + "");
        editTextPort2.setText(port2 + "");
    }

    private void forHideNotifattion() {
        startService(new Intent(Login2Activity.this, ForHideServiceIcon.class));
        stopService(new Intent(Login2Activity.this, ForHideServiceIcon.class));
    }

    private void inputAnimator() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(linearLayout,
                "scaleX", 1f, 0f);
        set.setDuration(500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }
}
