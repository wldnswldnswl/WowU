package com.capstone.wowu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.wowu.FlickeringAnimation;

public class TutorialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Thread thread = new Thread(); thread.start();

        // 6초 후 운동 시작 메시지 print
        Handler handler1=new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);

                TextView text=(TextView) findViewById(R.id.Text_tutorial_last);
                text.setText("곧 운동이 시작됩니다");
                text.setTextSize(16);
                new FlickeringAnimation().Flickering(text);

            }
        };
        handler1.sendEmptyMessageDelayed(0,6000);

        //10초 후 화면 전환
        Handler handler2=new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                startActivity(new Intent(TutorialActivity.this, PoseEstimationActivity.class));
                finish();
            }
        };
        handler2.sendEmptyMessageDelayed(0,10000);

        //skip 클릭시 타이머 완수 전 화면 전환
        Button b=(Button)findViewById(R.id.button_skip);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //thread.stopThread();
                //handler1.sendEmptyMessage(SEND_STOP);
                //handler2.sendEmptyMessage(SEND_STOP);
                Intent intent=new Intent(getApplicationContext(),PoseEstimationActivity.class);
                startActivity(intent);
            }
        });
    }
}
