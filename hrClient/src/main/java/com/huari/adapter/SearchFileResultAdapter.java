package com.huari.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.HistoryAnalysisActivity;
import com.huari.client.HistoryDFActivity;
import com.huari.client.HistoryPinDuanActivity;
import com.huari.client.PlayerActivity;
import com.huari.client.R;
import com.huari.dataentry.MessageEvent;
import com.huari.dataentry.MusicFileList;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFileResultAdapter extends RecyclerView.Adapter<SearchFileResultAdapter.viewholder> {
    public List<Integer> recordCheck = new ArrayList<>();

    public List<Integer> getRecordCheck() {
        return recordCheck;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public List<File> getFileList() {
        return fileList;
    }

    List<File> fileList;
    Context context;
    public static boolean longClickFlag;

    public SearchFileResultAdapter(List<File> files, Context context) {
        fileList = files;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item, null, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        if (longClickFlag == true) {
            holder.check.setVisibility(View.VISIBLE);
            if(recordCheck.contains(position)){
                holder.check.setSelected(true);
            }else {
                holder.check.setSelected(false);
            }
            holder.linearLayout.setOnClickListener(v -> {
                if(recordCheck.contains(position)){
                    recordCheck.remove(recordCheck.indexOf(position));
                }else {
                    recordCheck.add(position);
                }
                notifyDataSetChanged();
            });
        } else {
            recordCheck.clear();
            holder.check.setVisibility(View.GONE);
            holder.linearLayout.setOnClickListener(v -> {
                Intent intent;
                String type = fileList.get(position).getName().substring(0, 2);
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
                    case "RE":
                        intent = new Intent(context, PlayerActivity.class);
                        EventBus.getDefault().postSticky(new MessageEvent(fileList.get(position).getAbsolutePath(), position));
                        EventBus.getDefault().postSticky(new MusicFileList(fileList));
                        break;
                    default:
                        intent = new Intent(context, PlayerActivity.class);
                        break;
                }
                intent.putExtra("filename", fileList.get(position).getName());
                intent.putExtra("from", "history");
                context.startActivity(intent);
            });
        }
        holder.name.setText(fileList.get(position).getName());
        holder.time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(fileList.get(position).lastModified())));
        holder.number.setText(getSize(fileList.get(position).length()));
        holder.linearLayout.setOnLongClickListener(v -> {
            if(longClickFlag==false){
                longClickFlag = true;
                if(longClickListener!=null){
                    longClickListener.longClick();
                }
                notifyDataSetChanged();
            }
            return true;
        });
    }

    public void checkAllorNot(ImageView view){
        if(recordCheck.size() == fileList.size()){
            recordCheck.clear();
            view.setSelected(false);
        }else {
            recordCheck.clear();
            for(int i = 0;i<fileList.size();i++){
                recordCheck.add(i);
            }
            view.setSelected(true);
        }
        notifyDataSetChanged();
    }

    public interface LongClickListener{
        void longClick();
    }

    LongClickListener longClickListener;

    public void setLongClickListener(LongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView number;
        ImageView check;
        LinearLayout linearLayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.music_item);
            name = itemView.findViewById(R.id.music_item_name);
            time = itemView.findViewById(R.id.music_item_time);
            number = itemView.findViewById(R.id.music_item_number);
            check = itemView.findViewById(R.id.check_music);
        }
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
}
