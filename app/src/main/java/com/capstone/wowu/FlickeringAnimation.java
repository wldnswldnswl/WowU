package com.capstone.wowu;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class FlickeringAnimation {
    public void Flickering(TextView text) {
        //글자깜박임 애니메이션
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        text.startAnimation(anim);
    }
}
