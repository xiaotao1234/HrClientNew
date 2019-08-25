package com.huari.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.R;
import com.huari.client.RecordListActivity;
import com.huari.client.RecordShowOnewActivity;

import java.util.ArrayList;

/**
 * Created by Li on 2017/2/27.
 */

public class SimpleTestAdapter extends RecyclerView.Adapter<SimpleTestAdapter.TextViewHolder> {

    private String TAG = SimpleTestAdapter.class.getSimpleName();
    Context context;
    public SimpleTestAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, int position) {
        holder.textView.setText("2019-8-" + position);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        RecordShowOnewActivity.class);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return 100;
    }


    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout linearLayout;
        public TextViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            linearLayout = itemView.findViewById(R.id.recorder_item);
        }
    }

}
