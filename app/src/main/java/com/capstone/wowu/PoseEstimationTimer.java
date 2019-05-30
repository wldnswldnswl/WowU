package com.capstone.wowu;

import android.os.CountDownTimer;
import android.widget.TextView;

public class PoseEstimationTimer {
    private CountDownTimer countDownTimer;
    private long leftsec=10000;
    private static boolean timerRunning=true;
    private String timeLeft="";

//5/30 고침
    public void startTimer(TextView count, TextView intro){
        countDownTimer=new CountDownTimer(leftsec,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftsec-=1000;
                timeLeft=" "+((leftsec/1000)+1);
                count.setText(timeLeft);
                if(leftsec<=5) {
                    new FlickeringAnimation().Flickering(intro);
                    new FlickeringAnimation().Flickering(count);
                }
            //tt.invalidate();
        }

            @Override
            public void onFinish() {
                count.setText("");
                intro.setText("");
                countDownTimer.cancel();
                timerRunning=false;
            }
        }.start();
        timerRunning=true;
    }

}
