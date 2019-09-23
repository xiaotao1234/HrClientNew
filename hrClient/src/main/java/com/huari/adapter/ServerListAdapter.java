package com.huari.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huari.client.R;
import com.huari.client.ServerManagerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.viewholder> {

    boolean judge = false;
    List<String> serverNames;
    public ClickCallbackListener clickCallbackListener;
    Map<Integer, String> servicemap = new HashMap<>();


    public ServerListAdapter(List<String> serverNames, ClickCallbackListener clickCallbackListener,
                             checkListener checklistener,LongClickListener longClickListener) {
        this.serverNames = serverNames;
        this.clickCallbackListener = clickCallbackListener;
        this.checklistener = checklistener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.server_manager_list, viewGroup, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, int i) {
        if (judge == true) {
            viewholder.checkic.setVisibility(View.VISIBLE);
            viewholder.checkic.setOnClickListener(v ->
            {
                if (viewholder.checkic.isSelected() == false) {
                    viewholder.checkic.setSelected(true);
                    servicemap.put(i, serverNames.get(i));
                    checklistener.callback(getItemCount(), servicemap.keySet().size());
                } else {
                    viewholder.checkic.setSelected(false);
                    servicemap.remove(i);
                    checklistener.callback(getItemCount(), servicemap.keySet().size());
                }
            });
            viewholder.textView.setOnClickListener(v ->
            {
                if (viewholder.checkic.isSelected() == false) {
                    viewholder.checkic.setSelected(true);
                    servicemap.put(i, serverNames.get(i));
                    checklistener.callback(getItemCount(), servicemap.keySet().size());
                } else {
                    viewholder.checkic.setSelected(false);
                    servicemap.remove(i);
                    checklistener.callback(getItemCount(), servicemap.keySet().size());
                }
            });
        } else {
            viewholder.checkic.setVisibility(View.GONE);
            viewholder.textView.setOnClickListener(v -> clickCallbackListener.callback(i));
            viewholder.textView.setOnLongClickListener(v -> {
                judge = true;
                longClickListener.callback();
                notifyDataSetChanged();
                ServerManagerActivity.checkBack = true;
                return true;
            });
        }
        viewholder.textView.setText(serverNames.get(i));
    }

    @Override
    public int getItemCount() {
        return serverNames.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView checkic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            checkic = itemView.findViewById(R.id.check_ic);
            textView = itemView.findViewById(R.id.server_text);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setcheckboxtrue() {
        judge = false;
        notifyDataSetChanged();
    }

    public interface ClickCallbackListener {
        void callback(int i);
    }

    private checkListener checklistener;

    public interface checkListener {
        void callback(int size, int m);
    }

    private LongClickListener longClickListener;

    public interface  LongClickListener{
        void callback();
    }

    public List<String> getAllService() {
        List<String> list = new ArrayList<>();
        Set<Map.Entry<Integer, String>> enteryset = servicemap.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = enteryset.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> m = iterator.next();
            list.add(m.getValue());
        }
        return list;
    }
}
