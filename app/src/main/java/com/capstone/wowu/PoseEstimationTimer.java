//6/4 지운 수정
package com.capstone.wowu;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Timer;

public class PoseEstimationTimer {
    private CountDownTimer countDownTimer;
    long leftsec;
    private static boolean timerRunning=true;
    private String timeLeft="";

    public PoseEstimationTimer(){
        leftsec=10000;
    }
    // 자세인식 안내문구
    public void startTimer(TextView count, TextView intro){
        countDownTimer=new CountDownTimer(leftsec,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ImageClassifier.wait10sec=1;
                System.out.println("leftsec: "+leftsec);
                leftsec-=1000;
                timeLeft=" "+((leftsec/1000)+1);
                count.setText(timeLeft);
               // if(leftsec<3000) {
                    new FlickeringAnimation().Flickering(intro); //: 계속 깜박임
                    new FlickeringAnimation().Flickering(count);
             //   }
            //tt.invalidate();
        }

            @Override
            public void onFinish() {
                count.setText("");
                intro.setText("");
                countDownTimer.cancel();
                timerRunning=false;
                ImageClassifier.wait10sec=0;
            }
        }.start();
        //timerRunning=true;
    }

}
