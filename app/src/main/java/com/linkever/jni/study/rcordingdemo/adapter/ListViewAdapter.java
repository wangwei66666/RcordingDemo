package com.linkever.jni.study.rcordingdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linkever.jni.study.rcordingdemo.R;
import com.linkever.jni.study.rcordingdemo.bean.Recorder;

import java.util.List;


public class ListViewAdapter extends ArrayAdapter<Recorder> {
    private LayoutInflater mLayoutInfalter;
    private int min_length;
    private int max_length;

    public ListViewAdapter(Context context, List<Recorder> objects) {
        super(context, -1, objects);
        mLayoutInfalter = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        min_length = (int) (metrics.widthPixels * 0.15f);
        max_length = (int) (metrics.widthPixels * 0.72f);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if (convertView == null) {
            convertView = mLayoutInfalter.inflate(R.layout.item, parent, false);
            holder = new viewHolder();
            //这里获取的是背景框的FrameLayout的view对象，为了改变背景框的长度
            holder.length = convertView.findViewById(R.id.fl);
            holder.time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        Recorder item = getItem(position);
        int time = (int) Math.ceil(item.getTime());
        //注意转义字符
        holder.time.setText(time + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        //控制录音长度最长为：itemMinWidth + i temMaxWidth,以及类型转换
        if (time <= 60) {
            lp.width = (int) (min_length + max_length * time / 60f);
        } else {
            lp.width = min_length + max_length;
        }
        return convertView;
    }

    class viewHolder {
        View length;
        TextView time;
    }
}
