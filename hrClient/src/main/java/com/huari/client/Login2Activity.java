package com.huari.client;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    AVLoadingIndicatorView avLoadingIndicatorView;
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
        final int width = frameLayout.getMeasuredWidth();
        final int height = frameLayout.getMeasuredHeight();
        ipEditLayout.setHint("IP: ");
        port1EditLayout.setHint("Port1: ");
        port2EditLayout.setHint("Port2: ");
        loginTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextIp.setVisibility(View.INVISIBLE);
                editTextPort1.setVisibility(View.INVISIBLE);
                editTextPort2.setVisibility(View.INVISIBLE);
                inputAnimator(linearLayout, width, height);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        startActivity(new Intent(Login2Activity.this, Main2Activity.class));
                        break;
                }
            }
        };
    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(linearLayout,
                "scaleX", 1f, 0f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
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
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
//                progress.setVisibility(View.VISIBLE);
//                progressAnimator(progress);
//                mInputLayout.setVisibility(View.INVISIBLE);
                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                avLoadingIndicatorView.show();
//                avLoadingIndicatorView.setIndicatorColor(Color.parseColor("#1296DB"));
                handler.sendEmptyMessageDelayed(1, 2000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }
}
