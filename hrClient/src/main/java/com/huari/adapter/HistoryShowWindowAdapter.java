package com.huari.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huari.client.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryShowWindowAdapter extends RecyclerView.Adapter<HistoryShowWindowAdapter.viewholder> {

    List<String> list;

    public HistoryShowWindowAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_text_item,null,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item);
        }
    }
}
