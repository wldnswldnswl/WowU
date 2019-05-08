package com.capstone.wowu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InfoListAdapter extends BaseAdapter {
    private Context context;
    private List<infoItem> infoList;

    public InfoListAdapter(Context context, List<infoItem> infoList) {
        this.context = context;
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.infoitem, null);
        TextView infoText = (TextView)v.findViewById(R.id.infoText);
        ImageView infoImage = (ImageView)v.findViewById(R.id.infoImage);

        infoText.setText(infoList.get(position).getName());
        int id = context.getResources().getIdentifier(infoList.get(position).getImage(),  "drawable", context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);
        infoImage.setImageDrawable(drawable);

        return v;
    }
}
