package com.huari.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.huari.client.PlayerActivity;
import com.huari.client.R;
import com.huari.dataentry.FileSearchData;
import com.huari.dataentry.MessageEvent;
import com.huari.tools.FileOsImpl;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SearchFileListAdapter extends RecyclerView.Adapter<SearchFileListAdapter.viewHolder> {
    public List<String> fileNames = new ArrayList<>();
    public List<FileSearchData> fileList = new ArrayList<>();
    public List<File> files = new ArrayList<>();
    public boolean flag = true;
    FileOsImpl fileOs = new FileOsImpl();
    Context context;
    public int searchResultLength = 0;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_show_search_item, parent, false);
        viewHolder viewHolder = new viewHolder(view);
        this.context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (flag == true) {
            SpannableString spanString = new SpannableString(fileNames.get(position));
            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            spanString.setSpan(span, fileList.get(position).getStart(), fileList.get(position).getStart() + searchResultLength,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.textView.setText(spanString);
            holder.fullDirectory.setText(fileList.get(position).getFile().getAbsolutePath());
        } else {
            holder.textView.setText(fileNames.get(position));
            holder.fullDirectory.setText(files.get(position).getAbsolutePath());
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("xiao", fileNames.get(position));
            if (fileList.get(position).getFile().isDirectory()) {
                Log.d("xiaotao","click in");
                fileOs.setCurrentFloder(fileList.get(position).getFile());
                fileNames = fileOs.getFilesName();
                flag = false;
                files = fileOs.getFiles();
                notifyDataSetChanged();
            } else {
                if(flag==true){
                    EventBus.getDefault().postSticky(new MessageEvent(fileList.get(position).getFile().getAbsolutePath(), position));
                }else {
                    EventBus.getDefault().postSticky(new MessageEvent(files.get(position).getAbsolutePath(),position));
                }
                context.startActivity(new Intent(context, PlayerActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        TextView fullDirectory;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.file_box);
            textView = itemView.findViewById(R.id.file_name);
            fullDirectory = itemView.findViewById(R.id.search_file_directory);
        }
    }
}
