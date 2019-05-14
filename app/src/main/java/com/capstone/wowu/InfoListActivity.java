package com.capstone.wowu;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class InfoListActivity extends AppCompatActivity {

    private ListView infoListView;
    private InfoListAdapter adapter;
    private List<infoItem> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_list);

        // 툴바(상단바) 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        infoListView = (ListView) findViewById(R.id.infoListView);
        infoList = new ArrayList<infoItem>();
        infoList.add(new infoItem("squats", "스쿼트(Squats)"));
        infoList.add(new infoItem("wide_squats", "와이드 스쿼트(Wide Squats)"));
        infoList.add(new infoItem("plank", "플랭크(Plank)"));
        infoList.add(new infoItem("side_plank", "사이드 플랭크(Side Plank)"));
        infoList.add(new infoItem("side_hipkick", "사이드 힙 킥(Side Hip Kick)"));
        infoList.add(new infoItem("v_up", "V업(V-Up)"));
        infoList.add(new infoItem("cobra", "코브라 자세(Bhujangasana)"));
        infoList.add(new infoItem("cat", "고양이 자세(Marjaryasana)"));

        adapter = new InfoListAdapter(getApplicationContext(), infoList);
        infoListView.setAdapter(adapter);
        }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
