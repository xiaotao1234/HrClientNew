package com.huari.client;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.adapter.ServerListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServerManagerActivity extends AppCompatActivity {
    RecyclerView listRecyclerView;
    List<String> names = new ArrayList<>();
    private PopupWindow popupWindow;
    TextView textViewYes;
    TextView numTextShow;
    TextView textViewNo;
    RelativeLayout delete;

    public static boolean checkBack = false;
    private ServerListAdapter serverListAdapter;

    @Override
    public void onBackPressed() {
        if(checkBack==true){
            checkBack = false;
            serverListAdapter.setcheckboxtrue();
            delete.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_manager);
        numTextShow = findViewById(R.id.server_num_status);
        delete = findViewById(R.id.deletebu);
        listRecyclerView = findViewById(R.id.server_manager_list);
        listRecyclerView.setSystemUiVisibility(View.INVISIBLE);
        for(int i = 0;i<30;i++){
            names.add("dadadad");
        }
        serverListAdapter = new ServerListAdapter(
                names,
                i -> showPopwindow(i),
                (size, m) -> numTextShow.setText(m + " " + "/" + " " + size),
                () -> delete.setVisibility(View.VISIBLE)
        );
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listRecyclerView.setAdapter(serverListAdapter);
    }
    public void showPopwindow(int i){
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindow_item,null,false);
        textViewYes = view.findViewById(R.id.server_popwindow_yes);
        textViewNo = view.findViewById(R.id.server_popwindow_no);
        popupWindow = new PopupWindow();
        popupWindow = new PopupWindow(view,ActionBar.LayoutParams.WRAP_CONTENT,ActionBar.LayoutParams.WRAP_CONTENT,true);
        View parentview = LayoutInflater.from(ServerManagerActivity.this).inflate(R.layout.activity_server_manager,null);
        popupWindow.showAtLocation(parentview,Gravity.CENTER,0,0);
        textViewYes.setOnClickListener(v -> {
            Toast.makeText(ServerManagerActivity.this,"已关闭"+i,Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
        textViewNo.setOnClickListener(v -> {
            Toast.makeText(ServerManagerActivity.this,"已取消"+i,Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }
}
