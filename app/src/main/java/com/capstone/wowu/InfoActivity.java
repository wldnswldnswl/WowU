package com.capstone.wowu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class InfoActivity extends AppCompatActivity {
    YouTubePlayer.OnInitializedListener listener;
    Button button;
    YouTubePlayerSupportFragment frag;
    String name, image, difficulty, part, effect, way, note, video;
    TextView txv_toolbar, difficultyText, ex_partText, ex_effectText, ex_wayText, ex_noteText;
    ImageView infoImage;
    Drawable drawable;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 툴바(상단바) 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        name = getIntent().getStringExtra("name");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("exercise").child(name);
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        txv_toolbar = (TextView) findViewById(R.id.txv_toolbar);
        infoImage = (ImageView) findViewById(R.id.infoImage);
        difficultyText = (TextView) findViewById(R.id.difficultyText);
        ex_partText = (TextView) findViewById(R.id.ex_partText);
        ex_effectText = (TextView) findViewById(R.id.ex_effectText);
        ex_wayText = (TextView) findViewById(R.id.ex_wayText);
        ex_noteText = (TextView) findViewById(R.id.ex_noteText);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    image = dataSnapshot.child("image").getValue(String.class);
                    difficulty = dataSnapshot.child("difficulty").getValue(String.class);
                    part = dataSnapshot.child("part").getValue(String.class);
                    effect = dataSnapshot.child("effect").getValue(String.class);
                    way = dataSnapshot.child("way").getValue(String.class).replace("\\n","\n");
                    note = dataSnapshot.child("note").getValue(String.class);
                    video = dataSnapshot.child("video").getValue(String.class);
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://wowu-6e627.appspot.com").child("exercise/"+image);

                    txv_toolbar.setText(name);
                    Glide.with(getApplicationContext()).load(storageRef).into(infoImage);
                    infoImage.setImageDrawable(drawable);
                    difficultyText.setText(difficulty);
                    ex_partText.setText(part);
                    ex_effectText.setText(effect);
                    ex_wayText.setText(way);
                    ex_noteText.setText(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 유튜브 fragment 설정
        frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtubeView);
        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(video);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        button = (Button) findViewById(R.id.youtubeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag.initialize("AIzaSyA7RKP5kTGcV1SBmcb-9-KYTceCd-DYEM0", listener);
            }
        });
        progressDialog.dismiss();
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
