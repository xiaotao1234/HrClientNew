package com.huari.adapter;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huari.client.HistoryAnalysisActivity;
import com.huari.client.HistoryDFActivity;
import com.huari.client.HistoryPinDuanActivity;
import com.huari.client.PlayerActivity;
import com.huari.client.R;

import com.huari.dataentry.HistoryDataDescripe;
import com.huari.dataentry.MessageEvent;
import com.huari.dataentry.recentContent;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.viewholder> {
    List<HistoryDataDescripe> files;
    Context context;
    String type;

    public MusicListAdapter(List<HistoryDataDescripe> fileSavePath, Context context, String type) {
        this.files = fileSavePath;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.music_list_item, parent, false);
        return new viewholder(view);
    }

    public String getSize(long size) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        long m = size;
        while (size / 1024 > 0) {
            i++;
            m = size % 1024;
            size = size / 1024;
        }
        switch (i) {
            case 0:
                stringBuilder.append(size);
                stringBuilder.append(" ");
                stringBuilder.append("B");
                break;
            case 1:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f1 = new DecimalFormat("000");
                String ss = f1.format(m);
                stringBuilder.append(ss);
                stringBuilder.append(" ");
                stringBuilder.append("KB");
                break;
            case 2:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f2 = new DecimalFormat("000");
                String s2 = f2.format(m);
                stringBuilder.append(s2);
                stringBuilder.append(" ");
                stringBuilder.append("MB");
                break;
            case 3:
                stringBuilder.append(size);
                stringBuilder.append(".");
                Format f3 = new DecimalFormat("000");
                String s3 = f3.format(m);
                stringBuilder.append(s3);
                stringBuilder.append(" ");
                stringBuilder.append("GB");
                break;
        }
        return String.valueOf(stringBuilder);
    }


    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.musicCheck.setVisibility(View.GONE);
        holder.musicName.setText(files.get(position).getName());
//        holder.relativeLayout.setVisibility(View.GONE);
        holder.musicSize.setText(getSize(files.get(position).getSize()));
        Date date = new Date(files.get(position).getLastModifne());
        holder.musicTime.setText(date.toString());
//        holder.linearLayout.setOnClickListener(v -> {
//            context.startActivity(new Intent(context, PlayerActivity.class));
//            EventBus.getDefault().postSticky(new MessageEvent(files.
//                    get(position).getAbslitPath(), position));
//        });
//        holder.linearLayout.setLeftTouchListener(i -> {
//            Log.d("xiaoleft","come");
//            if(i == leftSlideView.LEFT&&holder.relativeLayout.getVisibility()==View.GONE){
//                holder.relativeLayout.setVisibility(View.VISIBLE);
//                notifyDataSetChanged();
//            }else if(i == leftSlideView.RIGHT&&holder.relativeLayout.getVisibility()==View.VISIBLE){
//                holder.relativeLayout.setVisibility(View.GONE);
//                notifyDataSetChanged();
//            }
//        });
        holder.relativeLayout.setOnClickListener(v -> {
            File file1 = new File(files.get(position).getAbslitPath());
            if (file1.delete()) {
                files.remove(position);
                holder.relativeLayout.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent;
            switch (type) {
                case "DF":
                    intent = new Intent(context, HistoryDFActivity.class);
                    break;
                case "AN":
                    intent = new Intent(context, HistoryAnalysisActivity.class);
                    break;
                case "PD":
                    intent = new Intent(context, HistoryPinDuanActivity.class);
                    break;
                case "REC":
                    intent = new Intent(context, PlayerActivity.class);
                    break;
                default:
                    intent = new Intent(context, PlayerActivity.class);
                    break;
            }
//            Bundle bundle = new Bundle();
            if (type == "REC") {
                EventBus.getDefault().postSticky(new MessageEvent(files.get(position).getAbslitPath(),position));
            }else {
                intent.putExtra("filename", files.get(position).getName());
                intent.putExtra("from", "history");
            }
            context.startActivity(intent);
        });
        holder.linearLayout.setOnLongClickListener(v -> {
            if (holder.relativeLayout.getVisibility() == View.GONE) {
                holder.relativeLayout.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            } else if (holder.relativeLayout.getVisibility() == View.VISIBLE) {
                holder.relativeLayout.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        ImageView musicCheck;
        ImageView typeIcon;
        TextView musicName;
        TextView musicTime;
        TextView musicSize;
        LinearLayout linearLayout;
        RelativeLayout relativeLayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.delete);
            musicCheck = itemView.findViewById(R.id.check_music);
            musicName = itemView.findViewById(R.id.music_item_name);
            musicTime = itemView.findViewById(R.id.music_item_time);
            musicSize = itemView.findViewById(R.id.music_item_number);
            linearLayout = itemView.findViewById(R.id.music_item);
            typeIcon = itemView.findViewById(R.id.type_icon);
        }
    }
}
