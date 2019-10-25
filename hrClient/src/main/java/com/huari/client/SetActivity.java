package com.huari.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.tools.FileOsImpl;
import com.huari.tools.SysApplication;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SetActivity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    ConstraintLayout constraintLayout1;
    ConstraintLayout constraintLayoutbig;
    Dialog dialog;
    private ImageView back;
    private FrameLayout frameParent;
    private LinearLayout linearLayout;
    private PopupWindow window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
    }

    private void initView() {
        back = findViewById(R.id.back);
        dialog = new Dialog(this);
        constraintLayout = findViewById(R.id.clear_record_data);
        constraintLayout1 = findViewById(R.id.clear_data);
        constraintLayoutbig = findViewById(R.id.big_layout);
        back.setOnClickListener(v -> finish());
        constraintLayout.setOnClickListener(v -> showGuideView(1));
        constraintLayout1.setOnClickListener(v -> showGuideView(2));
    }

    public void showGuideView(int id) {

        View view = constraintLayoutbig;
        if (view == null) return;

        ViewParent viewParent = view.getParent();
        if (viewParent instanceof FrameLayout) {
            //整个父布局
            frameParent = (FrameLayout) viewParent;

            //新建一个LinearLayout
            linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(Color.parseColor("#88000000"));//背景设置灰色透明
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

//            Rect rect = new Rect();
//            Point point = new Point();
//            nearby.getGlobalVisibleRect(rect, point);//获得nearby这个控件的宽高以及XY坐标 nearby这个控件对应就是需要高亮显示的地方
//
//            ImageView topGuideview = new ImageView(this);
//            topGuideview.setLayoutParams(new ViewGroup.LayoutParams(rect.width(), rect.height()));
//            topGuideview.setBackgroundResource(R.drawable.iv_topguide);
//
//            Rect rt = new Rect();
//            getWindow().getDecorView().getWindowVisibleDisplayFrame(rt);
//            topGuideview.setY(point.y - rt.top);//rt.top是手机状态栏的高度
//            ImageView bottomGuideview = new ImageView(this);
//            bottomGuideview.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
//            bottomGuideview.setBackgroundResource(R.drawable.iv_bottomguide);
//            bottomGuideview.setY(point.y + topGuideview.getHeight());
//
//            linearLayout.addView(topGuideview);
//            linearLayout.addView(bottomGuideview);
            frameParent.addView(linearLayout);
            popWindow(id);
        }
    }


    private void deleteBrowse() {
        try {
            File file = new File(FileOsImpl.forSaveFloder + File.separator + "data");
            if (file.exists()) {
                file = new File(file.getAbsolutePath() + File.separator + "mimi");
                if (file.exists()) {
                    file.delete();
                    FileOsImpl.recentUseFiles.clear();
                }
            }
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("xiao", "删除文件出错了");
        }
    }

    private void deleteRecord() {
        try {
            File file = new File(FileOsImpl.forSaveFloder + File.separator + "data");
            if (file.exists()) {
                for (File file1 : file.listFiles()) {
                    if (file1.getName().contains("AN") || file1.getName().contains("RE") || file1.getName().contains("PD") || file1.getName().contains("DF")) {
                        file1.delete();
                    }
                }
            }
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        constraintLayout.setSystemUiVisibility(View.INVISIBLE);
    }

    private void popWindow(int id) {
        View popupView = SetActivity.this.getLayoutInflater().inflate(R.layout.delete_warn, null);
        popupView.setPadding(50, 0, 50, 0);
        TextView oktext = popupView.findViewById(R.id.ok_button);
        TextView canceltext = popupView.findViewById(R.id.cancel_button);
        TextView textviewShow = popupView.findViewById(R.id.text_show);
        popupView.setPadding(50, 0, 50, 0);
        window = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, WRAP_CONTENT, true);
        window.setWidth((int) getResources().getDimension(R.dimen.dp_280));
        window.showAtLocation(constraintLayoutbig, Gravity.CLIP_VERTICAL, 0, 0);
        window.setAnimationStyle(R.style.popup_window_anim);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
        oktext.setOnClickListener(v -> {
            if (id == 1) {
                cancel();
                deleteRecord();
            } else {
                cancel();
                deleteBrowse();
            }
        });
        if (id == 1) {
            textviewShow.setText("是否删除所有本地数据？");
        } else {
            textviewShow.setText("是否删除数据浏览历史？");
        }
        linearLayout.setOnClickListener(v -> cancel());
        canceltext.setOnClickListener(v -> cancel());
    }

    private void cancel() {
        window.dismiss();
        frameParent.removeView(linearLayout);
    }
}
