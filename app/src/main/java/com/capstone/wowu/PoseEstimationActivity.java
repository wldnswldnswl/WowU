package com.capstone.wowu;

import android.content.Intent;
import android.graphics.Canvas;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;

import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.poseestimationmodel.PoseEstimationOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictor;
import ai.fritz.vision.poseestimation.FritzVisionPoseResult;

public class PoseEstimationActivity extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {
    
    private static final String TAG = PoseEstimationActivity.class.getSimpleName();

    private static final Size DESIRED_PREVIEW_SIZE = new Size(400, 600);

    private AtomicBoolean computing = new AtomicBoolean(false);

    private PredictorType predictorType;

    private FritzVisionPosePredictor posePredictor;
    private FritzVisionPoseResult poseResult;

    private int imageRotation;
    

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent callingIntent = getIntent();
        predictorType = PredictorType.valueOf(callingIntent.getStringExtra(Navigation.PREDICTOR_TYPE_KEY));
        Log.d(TAG, predictorType.name());
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final Size cameraSize, final int rotation) {
        imageRotation = FritzVisionOrientation.getImageRotationFromCamera(this, cameraId);
        FritzOnDeviceModel onDeviceModel = new PoseEstimationOnDeviceModel();
        posePredictor = FritzVision.PoseEstimation.getPredictor(onDeviceModel);

        addCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (poseResult != null) {
                            poseResult.drawPoses(canvas, cameraSize);
                        }
                    }
                });
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }

        if (!computing.compareAndSet(false, true)) {
            image.close();
            return;
        }
        final long startTime = SystemClock.uptimeMillis();
        final FritzVisionImage fritzVisionImage = FritzVisionImage.fromMediaImage(image, imageRotation);
        Log.d(TAG, "Image Creation:" + (SystemClock.uptimeMillis() - startTime));

        image.close();

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Start background task:" + (SystemClock.uptimeMillis() - startTime));
                        poseResult = posePredictor.predict(fritzVisionImage);
                        Log.d(TAG, "INFERENCE TIME:" + (SystemClock.uptimeMillis() - startTime));

                        requestRender();
                        computing.set(false);
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onSetDebug(final boolean debug) {

    }
}
