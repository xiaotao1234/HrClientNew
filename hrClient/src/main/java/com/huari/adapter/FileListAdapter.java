package com.huari.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huari.client.PlayerActivity;
import com.huari.client.R;
import com.huari.tools.SysApplication;
import com.huari.dataentry.MessageEvent;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.viewholder> {
    public File currentFile = SysApplication.fileOs.getCurrentFloder();
    public List<String> filesname;
    Context context;
    public List<File> files;
    SharedPreferences sharedPreferences;

    public FileListAdapter(List<String> filesname, Context context, List<File> files) {
        this.filesname = filesname;
        this.files = files;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_show_item, parent, false);
        viewholder viewholder1 = new viewholder(view);
        return viewholder1;
    }

    @Override
    public int getItemCount() {
        return filesname.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.file_box);
            textView = itemView.findViewById(R.id.file_name);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, final int position) {
        holder.textView.setText(filesname.get(position));
        if (files.get(position).isDirectory() == true) {
            holder.imageView.setImageResource(R.drawable.floder_icon);
        }
        holder.itemView.setOnClickListener(v -> {
            currentFile = SysApplication.fileOs.getCurrentFloder();
            if (files.get(position).isDirectory() == true) {
                SysApplication.fileOs.pushStack(currentFile);
                currentFile = files.get(position);
                SysApplication.fileOs.setCurrentFloder(currentFile);
                files = SysApplication.fileOs.getFiles();
                filesname = SysApplication.fileOs.getFilesName();
                notifyDataSetChanged();
            } else {
                EventBus.getDefault().postSticky(new MessageEvent(files.get(position).getAbsolutePath(),position));
                context.startActivity(new Intent(context, PlayerActivity.class));
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if(files.get(position).isDirectory()){
                sharedPreferences = context.getSharedPreferences("User",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("RootDirectory",files.get(position).getAbsolutePath());
                editor .commit();
                SysApplication.fileOs.setOsDicteoryPath(files.get(position));
                SysApplication.fileOs.setCurrentFloder(files.get(position));
                files = SysApplication.fileOs.getFiles();
                filesname = SysApplication.fileOs.getFilesName();
                SysApplication.fileOs.fileStack.clear();
                notifyDataSetChanged();
            }else {
                Toast.makeText(context,"不能使用文件作为主目录",Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
}
