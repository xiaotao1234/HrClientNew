package com.huari.client;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity {
    EditText ipEdittext;
    EditText port1Edittext;
    EditText port2Edittext;
    LinearLayout portContain;
    ImageView loginImage;AVLoadingIndicatorView avLoadingIndicatorView;
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
        setContentView(R.layout.activity_login);
        initViews();
        initEvents();
    }

    private void initEvents() {
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == LINKFAILED) {
                    Toast.makeText(LoginActivity.this, "连接服务器失败",
                            Toast.LENGTH_SHORT).show();
                    setTitle("未登录");
                    GlobalData.mainTitle = "未登录";
                } else if (msg.what == LINKSUCCESS) {
                    Toast.makeText(LoginActivity.this, "连接服务器成功",
                            Toast.LENGTH_SHORT).show();
                    setTitle("已登录");
                    GlobalData.mainTitle = "已登录";
                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    loginImage.setVisibility(View.VISIBLE);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            };
        };
        loginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Main2Activity.class));

//                saveStationCount = preferences.getInt("savecount", -1);
//                if (saveStationCount == -1) {
//                    seditor.putInt("savecount", 0);
//                }
//                GlobalData.mainIP = ipEdittext.getText().toString();
//                GlobalData.port1 = Integer.parseInt(port1Edittext.getText().toString());
//                GlobalData.port2 = Integer.parseInt(port2Edittext.getText().toString());
//                serviceIntent = new Intent();
//                serviceIntent.setAction("com.huari.service.mainservice");
//                try {
//                    ip = ipEdittext.getText().toString();
//                    port1 = Integer.parseInt(port1Edittext
//                            .getText().toString());
//                    port2 = Integer.parseInt(port2Edittext
//                            .getText().toString());
//                    seditor.putInt("port1", port1);
//                    seditor.putInt("port2", port2);
//                    seditor.putString("ip", ip);
//                    GlobalData.mainIP = ip;
//                    GlobalData.port1 = port1;
//                    GlobalData.port2 = port2;
//                    seditor.commit();
//
//                    if (GlobalData.toCreatService == false) {
//                        new Thread() {
//                            public void run() {
//                                startService(serviceIntent);
//                                MainService.startFunction();
//                                GlobalData.toCreatService = true;
//                            }
//                        }.start();
//                    }
//                } catch (Exception e) {
//                    GlobalData.mainIP = ip;
//                    GlobalData.port1 = port1;
//                    GlobalData.port2 = port2;
//                }
//
//                loginImage.setVisibility(View.INVISIBLE);
//                avLoadingIndicatorView.setVisibility(View.VISIBLE);
//                avLoadingIndicatorView.show();
//                avLoadingIndicatorView.setIndicatorColor(Color.parseColor("#1296DB"));
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) portContain.getLayoutParams();
//                int width = layoutParams.width;
//                int height = layoutParams.height;
//                LinearInterpolator linearInterpolator = new LinearInterpolator();
//                Animation animation = new ScaleAnimation(1,0,1,0);
//                animation.setDuration(1000);
//                animation.setInterpolator(linearInterpolator);
//                ipEdittext.setAnimation(animation);
//
//                AnimatorSet set = new AnimatorSet();
//                ValueAnimator animator = ValueAnimator.ofFloat(0, width);
//                ObjectAnimator animator2 = ObjectAnimator.ofFloat(portContain,
//                        "scaleX", 1f, 0f);
//                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        float value = (Float) animation.getAnimatedValue();
//                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) portContain
//                                .getLayoutParams();
//                        params.leftMargin = (int) value;
//                        params.rightMargin = (int) value;
//                        portContain.setLayoutParams(params);
//                    }
//                });
//                set.setDuration(1000);
//                set.setInterpolator(new AccelerateDecelerateInterpolator());
//                set.playTogether(animator2);
//                set.start();
//                set.addListener(new Animator.AnimatorListener() {
//
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        /**
//                         * 动画结束后，先显示加载的动画，然后再隐藏输入框
//                         */
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//                });

            }
        });
    }

    private void initViews() {
        portContain = findViewById(R.id.port_edit_contain);
        ipEdittext = findViewById(R.id.ip_edit);
        port1Edittext = findViewById(R.id.port1_edit);
        port2Edittext = findViewById(R.id.port2_edit);
        loginImage = findViewById(R.id.login_button);
        ipEdittext.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        port1Edittext.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        port2Edittext.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        avLoadingIndicatorView = findViewById(R.id.login_animtion);
        ipEdittext.setSystemUiVisibility(View.INVISIBLE);
        preferences = getSharedPreferences("myclient", MODE_PRIVATE);
        seditor = preferences.edit();
        ip = preferences.getString("ip", "192.168.1.1");
        port1 = preferences.getInt("port1", 5000);
        port2 = preferences.getInt("port2", 5012);
        ipEdittext.setText(ip);
        port1Edittext.setText(port1 + "");
        port2Edittext.setText(port2 + "");
    }

    @Override
    protected void onDestroy() {
        MainService.stopFunction();
        stopService(serviceIntent);
        super.onDestroy();
    }


}
