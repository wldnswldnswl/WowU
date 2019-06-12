/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.capstone.wowu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.wowu.view.AutoFitTextureView;
import com.capstone.wowu.view.DrawView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.capstone.wowu.view.DrawView.arr;
import static com.capstone.wowu.view.DrawView.isSquart;
import static com.capstone.wowu.view.DrawView.myView;
import static com.capstone.wowu.view.DrawView.squart;
import static com.capstone.wowu.view.DrawView.wsquart;

/**
 * Basic fragments for the Camera.
 */
public class Camera2BasicFragment<countDownTimer> extends Fragment
        implements FragmentCompat.OnRequestPermissionsResultCallback, TextToSpeech.OnInitListener {

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "TfLiteCameraDemo";

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String HANDLE_THREAD_NAME = "CameraBackground";

    private static final int PERMISSIONS_REQUEST_CODE = 1;

    public static TextView percentageText;

    public static TextView percentageText1;

    public static TextView percentageText2;

    public static TextView percentageText3;

    public static TextView percentageText4;

    public static TextView percentageText5;

    public static TextView percentageText6;

    //6/4 지운 수정
    public static TextView text;

    //수정
    public static TimerTask tt;

    //<수정>해원-세트 수 입력 버튼
    public static Button v_up;
    public static Button lunge;
    public static Button side_plank;
    public static Button side_hip_kick;
    public static Button squat;
    public static Button sphinx_pose;
    public static Button wide_squat;
    public static Button bhujangasana;
    public static Button plank;
    public static Button dhanurasana;

//    public static LinearLayout bottomInfoLayout;

    private final Object lock = new Object();
    private boolean runClassifier = false;
    private boolean checkedPermissions = false;
    private ImageClassifier classifier;
    public static int previewWidth;
    private Integer cameraPosition = CameraCharacteristics.LENS_FACING_BACK;
    private static TextToSpeech textToSpeech;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
     * TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                }
            };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String cameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView textureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession captureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice cameraDevice;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback stateCallback =
            new CameraDevice.StateCallback() {

                @Override
                public void onOpened(@NonNull CameraDevice currentCameraDevice) {
                    // This method is called when the camera is opened.  We start camera preview here.
                    cameraOpenCloseLock.release();
                    cameraDevice = currentCameraDevice;
                    createCameraPreviewSession();

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice currentCameraDevice) {
                    cameraOpenCloseLock.release();
                    currentCameraDevice.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice currentCameraDevice, int error) {
                    cameraOpenCloseLock.release();
                    currentCameraDevice.close();
                    cameraDevice = null;
                    Activity activity = getActivity();
                    if (null != activity) {
                        activity.finish();
                    }
                }
            };

    /** Current indices of device and model. */

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler backgroundHandler;

    /**
     * An {@link ImageReader} that handles image capture.
     */
    private ImageReader imageReader;

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder previewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #previewRequestBuilder}
     */
    private CaptureRequest previewRequest;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to capture.
     */
    private CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureProgressed(
                        @NonNull CameraCaptureSession session,
                        @NonNull CaptureRequest request,
                        @NonNull CaptureResult partialResult) {
                }

                @Override
                public void onCaptureCompleted(
                        @NonNull CameraCaptureSession session,
                        @NonNull CaptureRequest request,
                        @NonNull TotalCaptureResult result) {
                }
            };

    public static Camera2BasicFragment newInstance() {
        return new Camera2BasicFragment();
    }

    /**
     * Layout the preview and buttons.
     */

    //6/4 지운 수정
    //<수정>해원-세트 수 입력 버튼
    public static int flag_poseSelect=0;
    public static int flag_v_up=0;
    public static int flag_lunge=0;
    public static int flag_side_plank=0;
    public static int flag_side_hip_kick=0;
    public static int flag_squat=0;
    public static int flag_sphinx_pose=0;
    public static int flag_wide_squat=0;
    public static int flag_bhujangasana=0;
    public static int flag_plank=0;
    public static int flag_dhanurasana=0;

    //6/4 지운 수정
    private static String standard=null;
    public static void setViewText(String fromAnotherClass){
            Log.d("언제 실행되는지","");
            standard=fromAnotherClass;
            speech(standard);
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera2_basic, container, false);
        Button switchButton = view.findViewById(R.id.switchCameraButton);
        textToSpeech = new TextToSpeech(getActivity(),this);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });


        TextView countdown=(TextView)view.findViewById(R.id.text_count);
        TextView intro=(TextView)view.findViewById(R.id.text_view);
        TextView selection=(TextView)view.findViewById(R.id.text_selection);

        //6/4 지운 수정 (두 줄 삭제)

        int countPose=0;

        ImageView poseSelect;
        LinearLayout poseName;
        LinearLayout set_count_input;

        poseSelect = view.findViewById(R.id.pose);
        poseName = view.findViewById(R.id.pose_select_name);
        set_count_input=view.findViewById(R.id.set_count_input);

        //6/4 지운 수정
        text=(TextView)view.findViewById(R.id.text_countPose);

        Log.i("핸들러 출력:","");

        //6/4 지운 수정 : 핸들러를 사용해 레이아웃에 접근해야 동적으로 레이아웃을 변경할 수 있다.
        Handler h=new Handler(){
            public void handleMessage(Message msg){
                String message=(String)msg.obj; // 메시지 문자열로 변환
                text.setText(message); //setText
                System.out.println("카운트 뷰 값"+(message));
            }
        };

        //6/4 지운 수정
        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg=Message.obtain(); //메시지에 String 넣는 방법
                msg.obj=standard; // 1) obj에 문자열 대입
                msg.setTarget(h); // 2) 타겟 핸들러를 지정
                msg.sendToTarget(); // 3) 지정 타겟으로 해당 메시지 보내기
                //ImageClassifier.down=0;
            }

            @Override
            public boolean cancel() {
                Log.v(TAG,"timer cancel");
                return super.cancel();
            }
        };
        timer.schedule(timerTask, 0, 200);

        //6/4 지운 수정
        //<수정>해원-세트 수 입력 버튼
        v_up=view.findViewById(R.id.v_up);
        lunge=view.findViewById(R.id.lunge);
        side_plank=view.findViewById(R.id.side_plank);
        side_hip_kick=view.findViewById(R.id.side_hip_kick);
        squat=view.findViewById(R.id.squart);
        sphinx_pose=view.findViewById(R.id.sphinx_pose);
        wide_squat=view.findViewById(R.id.wide_squat);
        bhujangasana=view.findViewById(R.id.bhujangasana);
        plank=view.findViewById(R.id.plank);
        dhanurasana=view.findViewById(R.id.dhanurasana);

        //6/4 지운 수정 : 10초 후 화면 갱신 타이머
        Timer squat_timer= new Timer();
        TimerTask squat_task = new TimerTask() {
            @Override
            public void run() {
                ImageClassifier.setStandard(squart);
                myView.invalidate();
            }
        };

        Timer wide_squat_timer=new Timer();
        TimerTask wide_squat_task = new TimerTask() {
            @Override
            public void run() {
                ImageClassifier.setStandard(wsquart);
                myView.invalidate();
            }
        };

        //6/4 지운 수정 : 리스너들
        poseSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_poseSelect ==0) {
                    flag_poseSelect = 1;
                    poseName.setVisibility(View.VISIBLE);
                }
                else {
                    flag_poseSelect =0;
                    poseName.setVisibility(View.INVISIBLE);
                }
            }
        });

        //스쿼트 리스너
        //주희,해원 각 포즈 버튼 달기
        squat.setOnClickListener(new View.OnClickListener() {
            String squat_info = getString(R.string.squat_info);
            @Override
            public void onClick(View v) {
                if(flag_squat ==0) {
                   // wide_squat_timer.purge();
                    //((LinearLayout)selection.getParent()).removeView((TextView)selection); // 자세선택 안내문구 삭제
                    selection.setText("");
                    PoseEstimationTimer pose=new PoseEstimationTimer();
                    flag_squat = 1;
                    System.out.println("Camerabasic 스쿼트 플래그: "+flag_squat);
                    set_count_input.setVisibility(View.VISIBLE);
                    intro.setText("자세인식중");
                    pose.startTimer(countdown,intro);
                    squat_timer.schedule(squat_task, 10000,10000); // 화면 갱신 타이머 시작
                }
                else {
                    selection.setText("자세를 선택하세요");
                    intro.setText("");
                    countdown.setText("");
                    flag_squat =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                    squat_timer.cancel(); // 화면 갱신 타이머 종료
                }
                speech(squat_info);
            }
        });

        //와이드 스쿼트
        //주희,해원 각 포즈 버튼 달기
        wide_squat.setOnClickListener(new View.OnClickListener() {
            String wide_squat_info = getString(R.string.wide_squat_info);
            @Override
            public void onClick(View v) {
                if(flag_wide_squat ==0) {
                    selection.setText("");
                    PoseEstimationTimer pose2=new PoseEstimationTimer();
                    flag_wide_squat = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                    intro.setText("자세인식중");
                    pose2.startTimer(countdown,intro);
                    wide_squat_timer.schedule(wide_squat_task, 10000,10000);
                }
                else {
                    selection.setText("자세를 선택하세요");
                    intro.setText("");
                    countdown.setText("");
                    flag_wide_squat =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                    wide_squat_timer.cancel();
                }
                speech(wide_squat_info);
            }
        });

        //주희,해원 각 포즈 버튼 달기
        v_up.setOnClickListener(new View.OnClickListener() {
            String v_up_info = getString(R.string.v_up_info);
            @Override
            public void onClick(View v) {
                if(flag_v_up ==0) {
                    selection.setText("");
                    PoseEstimationTimer pose=new PoseEstimationTimer();
                    flag_v_up = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_v_up=0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }

                speech(v_up_info);
            }
        });

        //주희,해원  포즈 버튼 달기
        lunge.setOnClickListener(new View.OnClickListener() {
            String lunge_info ="다리를 골반넓이 만큼 벌리고 한쪽 발을 한발 앞으로 내미세요. 앞발의 무릎이 수직이 되고 허리를 곧게 세운 채로 내려가세요." ;
            @Override
            public void onClick(View v) {
                if(flag_lunge ==0) {
                    flag_lunge = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_lunge =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(lunge_info);
            }

        });
        //주희,해원 각 포즈 버튼 달기
        side_plank.setOnClickListener(new View.OnClickListener() {
            String side_plank_info = getString(R.string.side_plank_info);
            @Override
            public void onClick(View v) {
                if(flag_side_plank ==0) {
                    flag_side_plank = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_side_plank =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(side_plank_info);
            }
        });
        //주희,해원 각 포즈 버튼 달기
        side_hip_kick.setOnClickListener(new View.OnClickListener() {
            String side_hip_kick_info = getString(R.string.side_hip_kick_info);
            @Override
            public void onClick(View v) {
                if(flag_side_hip_kick ==0) {
                    flag_side_hip_kick = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_side_hip_kick =0;
                    set_count_input.setVisibility(View.INVISIBLE);

                }
                speech(side_hip_kick_info);
            }
        });

        //주희,해원 각 포즈 버튼 달기
        sphinx_pose.setOnClickListener(new View.OnClickListener() {
            String sphinx_pose_info = getString(R.string.sphinx_pose_info);
            @Override
            public void onClick(View v) {
                if(flag_sphinx_pose ==0) {
                    flag_sphinx_pose = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_sphinx_pose =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(sphinx_pose_info);
            }
        });

        //주희,해원 각 포즈 버튼 달기
        bhujangasana.setOnClickListener(new View.OnClickListener() {
            String bhujangasana_info = getString(R.string.bhujangasana_info);
            @Override
            public void onClick(View v) {
                if(flag_bhujangasana ==0) {
                    flag_bhujangasana = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_bhujangasana =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(bhujangasana_info);
            }
        });
        //주희,해원 각 포즈 버튼 달기
        plank.setOnClickListener(new View.OnClickListener() {
            String plank_info = getString(R.string.plank_info);
            @Override
            public void onClick(View v) {
                if(flag_plank ==0) {
                    flag_plank = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_plank =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(plank_info);
            }
        });
        //주희,해원 각 포즈 버튼 달기
        dhanurasana.setOnClickListener(new View.OnClickListener() {
            String dhanurasana_info = getString(R.string.dhanurasana_info);
            @Override
            public void onClick(View v) {
                if(flag_dhanurasana ==0) {
                    flag_dhanurasana = 1;
                    set_count_input.setVisibility(View.VISIBLE);
                }
                else {
                    flag_dhanurasana =0;
                    set_count_input.setVisibility(View.INVISIBLE);
                }
                speech(dhanurasana_info);
            }
        });

        return view;
    }

    //주희 speech 함수 설정
    public static void speech(String text) {
        // API 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            // API 20
        else
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //주희 음성출력 위한 oninit 함수 오버라이딩
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //사용할 언어를 설정
            int result = textToSpeech.setLanguage(Locale.KOREA);
            //언어 데이터가 없거나 혹은 언어가 지원하지 않으면...
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                //음성 톤
                textToSpeech.setPitch(0.7f);
                //읽는 속도
                textToSpeech.setSpeechRate(1.2f);
            }
        }
    }

    //주희 onstop 함수 설정
    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    /**
     * Connect the buttons to their event handler.
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        // Get references to widgets.
        textureView = view.findViewById(R.id.texture);
//        bottomInfoLayout = view.findViewById(R.id.bottom_info_view);
     /*   percentageText = view.findViewById(R.id.percentage);
        percentageText1 = view.findViewById(R.id.percentage1);
        percentageText2 = view.findViewById(R.id.percentage2);
        percentageText3 = view.findViewById(R.id.percentage3);
        percentageText4 = view.findViewById(R.id.percentage4);
        percentageText5 = view.findViewById(R.id.percentage5);
        percentageText6 = view.findViewById(R.id.percentage6);*/
        //6/4 지운 수정
        // text.setText(poseEstimation(countPose));
    }

 /*   public static void setPercentageText(List<String> percentageList, double percentage) {
        percentageText.post(new Runnable() {
            public void run() {
                if (percentage >= 90.0) percentageText.setBackgroundColor(Color.rgb(0, 255, 127));
                else percentageText.setBackgroundColor(Color.WHITE);
                percentageText.setText(String.format("%.2f", percentage) + "%");
                percentageText1.setText(percentageList.get(0));
                percentageText2.setText(percentageList.get(1));
                percentageText3.setText(percentageList.get(2));
                percentageText4.setText(percentageList.get(3));
                percentageText5.setText(percentageList.get(4));
                percentageText6.setText(percentageList.get(5));
            }
        });
    }*/

    /**
     * Load the model and labels.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startBackgroundThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (classifier != null) {
            classifier.close();
        }
        super.onDestroy();
    }


    @SuppressLint("MissingPermission")
    private void switchCamera() {
        if (cameraPosition.equals(CameraCharacteristics.LENS_FACING_BACK)) {
            cameraPosition = CameraCharacteristics.LENS_FACING_FRONT;
//            return;
        }
        else cameraPosition = CameraCharacteristics.LENS_FACING_BACK;

        setUpCameraOutputs(0, 0);
        closeCamera();
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing.equals(cameraPosition)) {
                    continue;
                }

                StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // // For still image captures, we use the largest available size.
                Size largest =
                        Collections.max(
                                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                imageReader =
                        ImageReader.newInstance(
                                largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/ 2);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);

                this.cameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to access Camera", e);
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
        }
    }

    private String[] getRequiredPermissions() {
        Activity activity = getActivity();
        try {
            PackageInfo info =
                    activity
                            .getPackageManager()
                            .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Opens the camera specified by {@link Camera2BasicFragment#cameraId}.
     */
    @SuppressLint("MissingPermission")
    private void openCamera(int width, int height) {
        if (!checkedPermissions && !allPermissionsGranted()) {
            FragmentCompat.requestPermissions(this, getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return;
        } else {
            checkedPermissions = true;
        }
        setUpCameraOutputs(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to open Camera", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            if (null != captureSession) {
                captureSession.close();
                captureSession = null;
            }
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != imageReader) {
                imageReader.close();
                imageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread(HANDLE_THREAD_NAME);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        // Start the classification train & load an initial model.
        synchronized (lock) {
            runClassifier = true;
        }
        backgroundHandler.post(periodicClassify);
        try {
            classifier = new ImageClassifierFloatBodypose(getActivity());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
            synchronized (lock) {
                runClassifier = false;
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted when stopping background thread", e);
        }
    }

    /**
     * Takes photos and classify them periodically.
     */
    private Runnable periodicClassify =
            new Runnable() {
                @Override
                public void run() {
                    synchronized (lock) {
                        if (runClassifier) {
                            classifyFrame();
                        }
                    }
                    backgroundHandler.post(periodicClassify);
                }
            };

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice.createCaptureSession(
                    Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == cameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            captureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                previewRequestBuilder.set(
                                        CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                // Finally, we start displaying the camera preview.
                                previewRequest = previewRequestBuilder.build();
                                captureSession.setRepeatingRequest(
                                        previewRequest, captureCallback, backgroundHandler);
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "Failed to set up config to capture Camera", e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//              showToast("Failed");
                        }
                    },
                    null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Failed to preview Camera", e);
        }
    }

    /**
     * Classifies a frame from the preview stream.
     */
    private void classifyFrame() {
        if (classifier == null || getActivity() == null || cameraDevice == null) {
            // It's important to not call showToast every frame, or else the app will starve and
            // hang. updateActiveModel() already puts a error message up with showToast.
            // showToast("Uninitialized Classifier or invalid context.");
            return;
        }
        SpannableStringBuilder textToShow = new SpannableStringBuilder();
        Bitmap bitmap = textureView.getBitmap(classifier.getImageSizeX(), classifier.getImageSizeY());

        classifier.classifyFrame(bitmap, textToShow);
        bitmap.recycle();
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    private static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    //timerTask함수
    /*public TimerTask t_task(){
        TimerTask temp_task = new TimerTask() {
            @Override
            public void run() {
                ImageClassifier.setStandard(squart);
                myView.invalidate();
            }
        };
        return temp_task;
    }*/
}