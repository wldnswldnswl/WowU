package com.capstone.wowu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class InfoListAdapter extends BaseAdapter {
    private Context context;
    private List<infoItem> infoList;
    TextView infoText;
    ImageView infoImage;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.infoitem, null);
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://wowu-6e627.appspot.com").child("exercise/"+infoList.get(position).getImage());

        infoText = (TextView)v.findViewById(R.id.infoText);
        infoImage = (ImageView)v.findViewById(R.id.infoImage);
        infoText.setText(infoList.get(position).getName());
        Glide.with(context).load(storageRef).into(infoImage);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, InfoActivity.class).putExtra("name", infoList.get(position).getName()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        return v;
    }
}