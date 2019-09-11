package com.huari.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huari.client.R;
import com.huari.dataentry.ClassBean;
import com.huari.dataentry.MyDevice;
import com.huari.dataentry.Station;

import java.util.List;


/**
 * Created by fan.feng on 2017/9/9.
 */

public class RecordAllAdapter extends RecordAllBaseAdapter<RecordAllAdapter.ClassHolder, RecordAllAdapter.StudentHolder> {

    private Context context;
    private List<Station> mContent;

    private LayoutInflater mInflater;

    //用于记录当前是隐藏还是显示
    private SparseBooleanArray mBooleanMap;

    public RecordAllAdapter(Context context, List mContent,StationFunctionListener stationFunctionListener) {
        this.context = context;
        this.mContent = mContent;
        this.stationFunctionListener = stationFunctionListener;
        mInflater = LayoutInflater.from(context);
        mBooleanMap = new SparseBooleanArray();
    }

    public interface StationFunctionListener{
        void callback(MyDevice device,Station station);
    }

    StationFunctionListener stationFunctionListener;

    @Override
    public int getHeadersCount() {
        return mContent.size();
    }

    @Override
    public int getContentCountForHeader(int headerPosition) {

        int count = mContent.get(headerPosition).getShowdevicelist().size();

        if (!mBooleanMap.get(headerPosition)) {
            count = 0;
        }
        return count;
    }

    /**
     * 创建头布局header的viewholder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ClassHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new ClassHolder(mInflater.inflate(R.layout.month_item, parent, false));
    }

    /**
     * 创建内容布局item的viewholder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public StudentHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new StudentHolder(mInflater.inflate(R.layout.day_item, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(ClassHolder holder, int position) {
        holder.linearLayout.setOnClickListener(null);
        holder.tvClassName.setText(mContent.get(position).getName());

        holder.linearLayout.setTag(position);
        holder.linearLayout.setOnClickListener(v -> {
            int position1 = (int) v.getTag();

            boolean isOpen = mBooleanMap.get(position1);

            mBooleanMap.put(position1, !isOpen);
            notifyDataSetChanged();
        });
    }

    @Override
    public void onBindContentViewHolder(StudentHolder holder, int HeaderPosition, int ContentPositionForHeader) {
        holder.tvName.setText(mContent.get(HeaderPosition).getShowdevicelist().get(ContentPositionForHeader).getName());
        holder.linearLayout.setOnClickListener(v -> stationFunctionListener.callback(mContent.get(HeaderPosition).getShowdevicelist().get(ContentPositionForHeader),
                mContent.get(HeaderPosition)));
    }



    class ClassHolder extends RecyclerView.ViewHolder{

        public TextView tvClassName;
        public LinearLayout linearLayout;

        public ClassHolder(View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvInfo);
            linearLayout = itemView.findViewById(R.id.recorder_item);
        }
    }

    class StudentHolder extends RecyclerView.ViewHolder{

        public TextView tvName;
        LinearLayout linearLayout;

        public StudentHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvInfo_day);
            linearLayout = itemView.findViewById(R.id.recorder_item);
        }
    }

}
