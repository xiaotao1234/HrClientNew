package com.huari.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnlyTextAdapter extends RecyclerView.Adapter<OnlyTextAdapter.viewholder> {

    private final TextView stationImage;
    private final TextView deviceImage;
    private final TextView mmImage;
    private int viewtype;
    public OnlyTextAdapter.forSaveSelect forSaveSelect;
    List<String> stringList;
    boolean kind = false;

    public class forSaveSelect {
        public String stationSelect;
        public String deviceSelect;
        public String mmSelect;
    }

    public void setStringList(List<String> stringList, int viewtype) {
        this.stringList = stringList;
        this.viewtype = viewtype;
    }

    public void setKind(boolean kind) {
        this.kind = kind;
    }


    public OnlyTextAdapter(List<String> stringList, TextView station, TextView device, TextView mm) {
        this.stringList = stringList;
        stationImage = station;
        deviceImage = device;
        mmImage = mm;
        forSaveSelect = new forSaveSelect();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if (viewtype == 2) {
            switch (stringList.get(position)) {
                case "DF":
                    if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("DF")){
                        ChangeBack(holder,position);
                    }
                    holder.textView.setText("单频测向");
                    holder.linearLayout.setOnClickListener(v -> {
                        mmImage.setText("单频测向");
                        forSaveSelect.mmSelect = "DF";
                        if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("DF")){
                            ChangeBack(holder,position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case "AN":
                    if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("AN")){
                        ChangeBack(holder,position);
                    }
                    holder.textView.setText("频谱分析");
                    holder.linearLayout.setOnClickListener(v -> {
                        mmImage.setText("频谱分析");
                        forSaveSelect.mmSelect = "AN";
                        if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("AN")){
                            ChangeBack(holder,position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case "PD":
                    if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("PD")){
                        ChangeBack(holder,position);
                    }
                    holder.textView.setText("频段扫描");
                    holder.linearLayout.setOnClickListener(v -> {
                        mmImage.setText("频段扫描");
                        forSaveSelect.mmSelect = "PD";
                        if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("PD")){
                            ChangeBack(holder,position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case "RE":
                    if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("RE")){
                        ChangeBack(holder,position);
                    }
                    holder.textView.setText("音频文件");
                    holder.linearLayout.setOnClickListener(v -> {
                        mmImage.setText("音频文件");
                        forSaveSelect.mmSelect = "RE";
                        if(forSaveSelect.mmSelect!=null&&forSaveSelect.mmSelect.equals("RE")){
                            ChangeBack(holder,position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
            }
        } else if (viewtype == 1) {
            if(forSaveSelect.deviceSelect!=null&&forSaveSelect.deviceSelect.equals(stringList.get(position))){
                ChangeBack(holder,position);
            }
            holder.textView.setText(stringList.get(position));
            holder.linearLayout.setOnClickListener(v -> {
                deviceImage.setText(stringList.get(position));
                forSaveSelect.deviceSelect = stringList.get(position);
                if(forSaveSelect.deviceSelect!=null&&forSaveSelect.deviceSelect.equals(stringList.get(position))){
                    ChangeBack(holder,position);
                    notifyDataSetChanged();
                }
            });
        } else if (viewtype == 0) {
            if(forSaveSelect.stationSelect!=null&&forSaveSelect.stationSelect.equals(stringList.get(position))){
                ChangeBack(holder,position);
            }
            holder.textView.setText(stringList.get(position));
            holder.linearLayout.setOnClickListener(v -> {
                stationImage.setText(stringList.get(position));
                forSaveSelect.stationSelect = stringList.get(position);
                if(forSaveSelect.stationSelect!=null&&forSaveSelect.stationSelect.equals(stringList.get(position))){
                    ChangeBack(holder,position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void ChangeBack(@NonNull viewholder holder,int position) {
        holder.linearLayout.setBackgroundColor(Color.parseColor("#22000000"));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout linearLayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.select_item);
            textView = itemView.findViewById(R.id.item_text);
        }
    }
}
