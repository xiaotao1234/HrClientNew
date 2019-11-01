package com.huari.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huari.client.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BottomDialogAdapter extends RecyclerView.Adapter {
    public void setmList(List<String> mList, int id) {
        this.mList = mList;
        this.id = id;
    }

    private List<String> mList;
    private LayoutInflater mInflater;
    private Context mContext;
    private int id;

    public BottomDialogAdapter(Context context, List list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mContext = context;
    }

    // 获取条目数量
    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.botton_list_item, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        NormalViewHolder vh = (NormalViewHolder) holder;
        if (position == 0) {
            vh.text.setTextColor(Color.parseColor("#0000FF"));
        }
        vh.text.setText(mList.get(position));
        if (mList.get(position).equals("重新下载")) {
            vh.text.setOnClickListener(v -> {
                if (downloadListener != null) {
                    downloadListener.download(id);
                }
            });
        } else if (mList.get(position).equals("删除")) {
            vh.text.setTextColor(Color.parseColor("#FF0000"));
            vh.text.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.delete(id);
                }
            });
        } else if (mList.get(position).equals("取消")) {

        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView text;

        public NormalViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_textview);
        }
    }

    DownloadListener downloadListener;
    DeleteListener deleteListener;
    BackListener backListener;

    public interface DownloadListener {
        void download(int id);
    }

    public interface DeleteListener {
        void delete(int id);
    }

    public interface BackListener{
        void back();
    }
    public void setDownloadListener(DownloadListener downloadListener, DeleteListener deleteListener,BackListener backListener) {
        this.downloadListener = downloadListener;
        this.deleteListener = deleteListener;
        this.backListener = backListener;
    }
}

