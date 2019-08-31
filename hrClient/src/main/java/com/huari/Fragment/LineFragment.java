package com.huari.Fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.huari.client.AllRecordQueryActivity;
import com.huari.client.Main2Activity;
import com.huari.client.R;
import com.huari.dataentry.GlobalData;
import com.huari.service.MainService;
import com.wang.avi.AVLoadingIndicatorView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LineFragment extends Fragment {


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
    Context context;

    public LineFragment() {

    }

    @SuppressLint("ValidFragment")
    public LineFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login2, container, false);
        frameLayout = view.findViewById(R.id.contact_edit_frame);
        linearLayout = view.findViewById(R.id.contact_edit_linearlayout);
        editTextIp = view.findViewById(R.id.ip_edit);
        editTextPort1 = view.findViewById(R.id.port1_edit);
        editTextPort2 = view.findViewById(R.id.port2_edit);
        loginTextButton = view.findViewById(R.id.main_btn_login);
        avLoadingIndicatorView = view.findViewById(R.id.login_animtion);
        ipEditLayout = view.findViewById(R.id.ip_edit_layout);
        port1EditLayout = view.findViewById(R.id.port1_edit_layout);
        port2EditLayout = view.findViewById(R.id.port2_edit_layout);
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
            serviceIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
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
                            context.startService(serviceIntent);
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
        preferences = context.getSharedPreferences("myclient", Context.MODE_PRIVATE);
        seditor = preferences.edit();
        ip = preferences.getString("ip", "192.168.1.249");
        port1 = preferences.getInt("port1", 5000);
        port2 = preferences.getInt("port2", 5012);
        editTextIp.setText(ip);
        editTextPort1.setText(port1 + "");
        editTextPort2.setText(port2 + "");
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == LINKFAILED) {
                    Toast.makeText(context, "连接服务器失败",
                            Toast.LENGTH_SHORT).show();
                    GlobalData.mainTitle = "未登录";
                } else if (msg.what == LINKSUCCESS) {
                    Toast.makeText(context, "连接服务器成功",
                            Toast.LENGTH_SHORT).show();
                    GlobalData.mainTitle = "已登录";
                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(context, AllRecordQueryActivity.class));
                }
            }
        };
        return view;
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
