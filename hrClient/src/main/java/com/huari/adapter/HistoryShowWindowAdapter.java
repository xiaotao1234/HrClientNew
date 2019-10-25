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
    List<String> list1;

    public HistoryShowWindowAdapter(List<String> list,List<String> list1) {
        this.list = list;
        this.list1 = list1;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_text_item,null,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.textView.setText(list.get(position)+":");
        holder.textViewContext.setText(list1.get(position));
    }

    @Override
    public int getItemCount() {
        int i = 0;
        for(String s:list){
            if(s.length()!=0){
                i++;
            }
        }
        return i;
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewContext;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item_name);
            textViewContext = itemView.findViewById(R.id.text_context);
        }
    }
}
