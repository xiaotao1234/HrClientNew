package com.huari.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.IquareActivity;
import com.huari.client.MapActivity;
import com.huari.client.R;
import com.huari.client.ServerManagerActivity;
import com.huari.dataentry.LogicParameter;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Station;

import java.util.ArrayList;
import java.util.List;

public class StationFunctionListAdapter extends RecyclerView.Adapter<StationFunctionListAdapter.viewholder> {
    MyDevice myDevice;
    List<LogicParameter> logicParameters;
    Station station;
    Context context;
    List<String> list;

    public StationFunctionListAdapter(MyDevice myDevice, Station station, Context context, List<String> list) {
        this.myDevice = myDevice;
        this.station = station;
        this.context = context;
        logicParameters = new ArrayList<>();
        this.list = list;
        for (String s : myDevice.getLogic().keySet()) {
            logicParameters.add(myDevice.getLogic().get(s));
        }
    }

    @NonNull

    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.station_function_item, viewGroup, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, int i) {
        if (i < myDevice.getLogic().size()) {
            viewholder.textView.setText(logicParameters.get(i).getType().startsWith("L") ? "频谱分析" :
                    (logicParameters.get(i).getType().startsWith("S") ? "频段扫描" :
                            (logicParameters.get(i).getType().startsWith("D") ? "单频测向" : "离散扫描")));
        } else {
            viewholder.textView.setText(list.get(i - myDevice.getLogic().size()));
        }
        viewholder.linearLayout.setOnClickListener(v -> skipActivity(i));
    }

    @Override
    public int getItemCount() {
        return myDevice.getLogic().size() + list.size();
    }

    class viewholder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.station_function_picture);
            textView = itemView.findViewById(R.id.station_function_text);
            linearLayout = itemView.findViewById(R.id.station_fuction_view);
        }

    }

    private void skipActivity(int i) {
        if (i < logicParameters.size()) {
            if (logicParameters.get(i).getType().startsWith("L")) {
                Intent intent = new Intent();
                intent.setAction("function0");
                Bundle bundle = new Bundle();
                bundle.putString("devicename",
                        myDevice.name);
                bundle.putString("stationname",
                        station.name);
                bundle.putString("stationKey",
                        station.id);
                bundle.putString("lid", logicParameters.get(i).id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else if (logicParameters.get(i).getType().startsWith("DDF")) {
                Intent intent = new Intent();
                intent.setAction("function12");
                Bundle bundle = new Bundle();
                bundle.putString("devicename",
                        myDevice.name);
                bundle.putString("stationname",
                        station.name);
                bundle.putFloat("lan",
                        station.lan);
                bundle.putFloat("lon",
                        station.lon);
                bundle.putString("stationKey",
                        station.id);
                bundle.putString("lid", logicParameters.get(i).id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else if (logicParameters.get(i).getType().startsWith("SCAN")) {
                Intent intent = new Intent();
                intent.setAction("function18");
                Bundle bundle = new Bundle();
                bundle.putString("devicename",
                        myDevice.name);
                bundle.putString("stationname",
                        station.name);
                bundle.putString("stationKey",
                        station.id);
                bundle.putString("lid", logicParameters.get(i).id);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        } else {
            if(i-logicParameters.size()==0){
                context.startActivity(new Intent(context,MapActivity.class));
            }
//            else
//            if (i - logicParameters.size() == 0) {
//                context.startActivity(new Intent(context, IquareActivity.class));
//            } else if (i - logicParameters.size() == 1) {
//                context.startActivity(new Intent(context, ServerManagerActivity.class));
//            }
        }
    }
}
