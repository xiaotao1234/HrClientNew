package com.huari.adapter;

import android.content.Context;
import android.content.Intent;


import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.HistoryAnalysisActivity;
import com.huari.client.HistoryDFActivity;
import com.huari.client.HistoryPinDuanActivity;
import com.huari.client.PlayerActivity;
import com.huari.dataentry.MessageEvent;
import com.huari.dataentry.recentContent;
import com.huari.client.R;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by Li on 2017/2/27.
 */

public class SimpleTestAdapter extends RecyclerView.Adapter<SimpleTestAdapter.TextViewHolder> {
    private List<recentContent> recentContent;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRecentContent(List<recentContent> recentContent) {
        this.recentContent = recentContent;
    }

    public SimpleTestAdapter(Context context) {
        this.context = context;
    }

    public SimpleTestAdapter() {
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, int position) {
        holder.textView.setText(getName(position));
        holder.titleTextView.setText(getTypename(position));
        holder.linearLayout.setOnClickListener(v -> {
            Intent intent;
            switch (getTypename(position)) {
                case "单频测量":
                    intent = new Intent(context, HistoryDFActivity.class);
                    break;
                case "频谱分析":
                    intent = new Intent(context, HistoryAnalysisActivity.class);
                    break;
                case "频段扫描":
                    intent = new Intent(context, HistoryPinDuanActivity.class);
                    break;
                case "音频":
                    intent = new Intent(context, PlayerActivity.class);
                    break;
                default:
                    intent = new Intent(context, HistoryDFActivity.class);
                    break;
            }
            if(getTypename(position)=="音频"){
                EventBus.getDefault().postSticky(new MessageEvent(recentContent.get(position).getFile(),position));
            }else {
                intent.putExtra("filename", new File(recentContent.get(position).getFile()).getName());
                intent.putExtra("from", "history");
            }
            context.startActivity(intent);
        });
    }

    private String getName(int position) {
        String filename = recentContent.get(position).getFilename();
        if (filename != null) {
            return filename.substring(9, 19);
        } else {
            return null;
        }
    }

    private String getTypename(int position) {
        String name;
        switch (recentContent.get(position).getType()) {
            case 1:
                name = "单频测量";
                break;
            case 2:
                name = "频谱分析";
                break;
            case 3:
                name = "频段扫描";
                break;
            case 4:
                name = "音频";
                break;
            default:
                name = null;
                break;
        }
        return name;
    }


    @Override
    public int getItemCount() {
        return recentContent.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView titleTextView;
        LinearLayout linearLayout;

        public TextViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            textView = itemView.findViewById(R.id.time);
            linearLayout = itemView.findViewById(R.id.recorder_item);
        }
    }
}
