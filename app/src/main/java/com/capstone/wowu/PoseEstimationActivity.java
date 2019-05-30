package com.capstone.wowu;

import android.app.Activity;
import android.os.Bundle;

/**
 * Main {@code Activity} class for the Camera app.
 */
public class PoseEstimationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (null == savedInstanceState) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.camera_container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }
}
