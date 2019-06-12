package com.capstone.wowu;

import android.os.CountDownTimer;
import android.widget.TextView;

public class CountFlag {
    private static CountDownTimer countDownTimer;
    static long leftsec;
    private String timeLeft = "";

    public CountFlag() {
        leftsec = 5000;
    }

    // 자세인식 안내문구
    public static void startTimer() {
        countDownTimer = new CountDownTimer(leftsec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ImageClassifier.down_flag = 0;
            }

            @Override
            public void onFinish() {
                ImageClassifier.down_flag = 1;
            }
        }.start();
    }
}
