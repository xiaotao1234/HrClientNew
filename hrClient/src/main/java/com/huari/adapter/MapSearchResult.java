package com.huari.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.huari.client.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MapSearchResult extends RecyclerView.Adapter<MapSearchResult.viewholder> {

    private Context context;

    public MapSearchResult(ArrayList<MKOLSearchRecord> records) {
        this.records = records;
    }

    ArrayList<MKOLSearchRecord> records;

    public void setRecords(ArrayList<MKOLSearchRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_map_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.tvTitle.setText(records.get(0).cityName);
        holder.size.setText(formatDataSize(records.get(0).dataSize));
        holder.status.setOnClickListener(v -> {
            if(clickListeners!=null){
                clickListeners.event(records.get(0).cityID);
                Toast.makeText(context,"开始下载"+records.get(0).cityName+"的离线地图数据",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView size;
        ImageView status;
        ProgressBar progressBar;
        ImageView imageView;
        RelativeLayout relativeLayout;

        public viewholder(View view) {
            super(view);
            relativeLayout = view.findViewById(R.id.lin);
            this.tvTitle = view.findViewById(R.id.city_name);
            this.size = view.findViewById(R.id.all_size);
            this.status = view.findViewById(R.id.download_status);
            this.progressBar = view.findViewById(R.id.probar);
            imageView = view.findViewById(R.id.expand);
        }
    }

    public String formatDataSize(long size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    public interface ClickListeners {
        void event(int id);
    }

    ClickListeners clickListeners;

    public void setClickListeners(ClickListeners clickListeners) {
        this.clickListeners = clickListeners;
    }
}
