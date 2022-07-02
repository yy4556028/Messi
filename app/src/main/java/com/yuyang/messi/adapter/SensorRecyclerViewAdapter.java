package com.yuyang.messi.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuyang.messi.R;

import java.util.List;

public class SensorRecyclerViewAdapter extends RecyclerView.Adapter<SensorRecyclerViewAdapter.MyHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private List<String> titles;
    private List<Class> classes;

    public SensorRecyclerViewAdapter(Context context, List<String> titles, List<Class> classes) {
        this.context = context;
        this.titles = titles;
        this.classes = classes;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new MyHolder(mInflater.inflate(R.layout.activity_sensor_recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder viewHolder, final int i) {
        viewHolder.textView.setText(titles.get(i));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, classes.get(i)));
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.activity_sensor_recyclerView_item_text);
        }
    }
}



