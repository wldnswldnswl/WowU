package com.capstone.wowu;

import android.content.Context;
import android.content.Intent;

/**
 * Navigation is a helper class for common links throughout the app.
 */
public class Navigation {

    public static final String PREDICTOR_TYPE_KEY = "PredictorType";

    public static void startPoseEstimation(Context context) {
        Intent fullCameraActivity = new Intent(context, PoseEstimationActivity.class);
        fullCameraActivity.putExtra(PREDICTOR_TYPE_KEY, PredictorType.POSE_ESTIMATION.name());

        context.startActivity(fullCameraActivity);
    }
}