package com.huari.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huari.client.R;

public class StationFunctionListAdapter extends RecyclerView.Adapter<StationFunctionListAdapter.viewholder> {
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.station_function_item,viewGroup,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, int i) {

    }

    @Override
    public int getItemCount() {
        return 30;
    }

    class viewholder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.station_function_picture);
            textView = itemView.findViewById(R.id.station_function_text);
        }
    }
}
