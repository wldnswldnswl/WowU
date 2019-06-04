package com.capstone.wowu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.rgb;

public class DrawView extends View{


    private static final int HEATMAPWIDTH = 96;
    private static final int HEATMAPHEIGHT = 96;
    private static final int NUMJOINT = 14;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    public static int MAX_PREVIEW_HEIGHT;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    public static int MAX_PREVIEW_WIDTH;
    private static int count = 0;
    private static int count1 = 0;

    public static View myView;
    private Paint paint[] = new Paint[14];
    private Paint ppaint;
    private static float[][] arr = new float[14][2];
    private static float[][] standard=new float[14][2];

    public static int poseCount=0;
    public static float angleBetween2Lines(PointF A1, PointF A2, PointF B1, PointF B2) {
        float angle1 = (float) Math.atan2(A2.y - A1.y, A1.x - A2.x);
        float angle2 = (float) Math.atan2(B2.y - B1.y, B1.x - B2.x);
        float calculatedAngle = (float) Math.toDegrees(angle1 - angle2);
        if (calculatedAngle < 0) calculatedAngle += 360;
        return calculatedAngle;
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        myView = DrawView.this;
        ppaint = new Paint();
        ppaint.setStrokeWidth(6);
        ppaint.setColor(rgb(135,206,250));

        for (int i = 0; i < NUMJOINT; i++) {
            paint[i] = new Paint();
            paint[i].setStyle(Paint.Style.STROKE);
            paint[i].setStrokeWidth(15);
        }
        //keypoint: 번호-해당 부위
        paint[0].setColor(Color.BLACK); // 0번 - 머리
        paint[1].setColor(Color.RED); // 1번 - 목
        paint[2].setColor(Color.GREEN); // 2번 - 오른쪽 어깨
        paint[3].setColor(Color.YELLOW); // 3번 - 오른쪽 팔꿈치
        paint[4].setColor(Color.BLUE); // 4번 - 오른쪽 손목
        paint[5].setColor(Color.DKGRAY); // 5번 - 왼쪽 어깨
        paint[6].setColor(Color.LTGRAY); // 6번 - 왼쪽 팔꿈치
        paint[7].setColor(Color.MAGENTA); // 7번 - 왼쪽 손목
        paint[8].setColor(Color.WHITE); // 8번 - 오른쪽 골반
        paint[9].setColor(Color.BLUE); // 9번 - 오른쪽 무릎
        paint[10].setColor(Color.RED); // 10번 - 오른쪽 발목
        paint[11].setColor(Color.GREEN); // 11번 - 왼쪽 골반
        paint[12].setColor(Color.YELLOW); // 12번 - 왼쪽 무릎
        paint[13].setColor(Color.BLUE); // 13번 - 왼쪽 발목

        setFocusable(true);
    }
    public double getAngle(double startx,double starty, double endx,double endy) {
        double dy = endy-starty;
        double dx = endx-startx;
        return Math.atan2(dy, dx) * (180.0 / Math.PI);
    }
    /*스쿼트*/
  /*  protected void squartPose(){

        //높이차
        float heightRight=arr[11][0]-arr[12][0];
        float heigtLeft=arr[8][0]-arr[9][0];

        //너비차
        float widthRight=arr[11][1]-arr[11][1];
        float widthLeft=arr[8][1]-arr[9][1];

        //무릎-발목: 8번(11번)의 y위치를 9번(12번)과 같게
        double len8_9=Math.sqrt(Math.pow((arr[8][1]-arr[9][1]),2)+Math.pow((arr[8][0]-arr[9][0]),2));
        arr[8][0]=arr[9][0];
        arr[8][1]=arr[9][1]+(float)len8_9;

        double len11_12=Math.sqrt(Math.pow((arr[11][1]-arr[12][1]),2)+Math.pow((arr[11][0]-arr[12][0]),2));
        arr[11][0]=arr[12][0];
        arr[11][1]=arr[12][1]+(float)len11_12;

        //상체(머리,목,어깨)높이: 엉덩이 내려간 만큼 같이 내려가게
        arr[0][0]=arr[0][0]-heigtLeft;
        arr[1][0]=arr[1][0]-heigtLeft;
        arr[2][0]=arr[2][0]-heigtLeft;
        arr[5][0]=arr[5][0]-heightRight;

        //arr[0][1]=arr[0][1]-heigtLeft/2;
        //arr[1][1]=arr[1][1]-heigtLeft/2;
        //arr[2][1]=arr[2][1]-heigtLeft/2;
        //arr[5][1]=arr[5][1]-heightRight/2;


        //손 뻗는 모양: 팔꿈치(3번,6번), 손목(4번,7번)-> 어깨(2번,5번)과 높이가 같게
        arr[3][0]=arr[2][0];
        arr[4][0]=arr[2][0];
        arr[6][0]=arr[5][0];
        arr[7][0]=arr[5][0];

        //무릎 앞으로
        //arr[9][1]=arr[9][1]+widthRight/2;
       // arr[12][1]=arr[12][1]+widthRight/2;

    }*/

    /*스쿼트*/
    public static void squatPose() {
        //높이차
        float heightRight = arr[11][0] - arr[12][0];
        float heigtLeft = arr[8][0] - arr[9][0];

        //무릎-발목: 8번(11번)의 y위치를 9번(12번)과 같게
        double len8_9 = Math.sqrt(Math.pow((arr[8][1] - arr[9][1]), 2) + Math.pow((arr[8][0] - arr[9][0]), 2));
        arr[8][0] = arr[9][0];
        arr[8][1] = arr[9][1] + (float) len8_9; //왼쪽 측면 그려질 때

        double len11_12 = Math.sqrt(Math.pow((arr[11][1] - arr[12][1]), 2) + Math.pow((arr[11][0] - arr[12][0]), 2));
        arr[11][0] = arr[12][0];
        arr[11][1] = arr[12][1] - (float) len11_12; //오른쪽 측면 그려질 때

        float x, y;
        x = (arr[8][1] + arr[11][1]) / 2 + 5;
        y = (arr[8][0] + arr[11][0]) / 2 + 5; //

        //상체(머리,목,어깨)높이: 엉덩이 내려간 만큼 같이 내려가게
        arr[0][0] = arr[0][0] - heigtLeft;
        arr[1][0] = arr[1][0] - heigtLeft;
        arr[2][0] = arr[2][0] - heigtLeft;
        arr[5][0] = arr[5][0] - heightRight;

        //허벅지 길이 조정
        arr[8][1] = arr[8][1] - (float)0.2*(float)len8_9;

        //목 위치 조정
        arr[1][1] = (float)0.5*(arr[9][1]+arr[8][1]);

        //머리랑 목 같게
        arr[0][1] = arr[1][1];

        //목과 어깨 위치가 같게 (측면 고려)
        arr[2][0] = arr[1][0];
        arr[2][1] = arr[1][1];
        arr[5][0] = arr[1][0];
        arr[5][1] = arr[1][1];

        //어깨와 팔꿈치, 팔꿈치와 손목간의 길이 측정
        double len2_3 = Math.sqrt(Math.pow((arr[2][1] - arr[3][1]), 2) + Math.pow((arr[2][0] - arr[3][0]), 2));
        double len3_4 = Math.sqrt(Math.pow((arr[3][1] - arr[4][1]), 2) + Math.pow((arr[3][0] - arr[4][0]), 2)); //left
        double len5_6 = Math.sqrt(Math.pow((arr[5][1] - arr[6][1]), 2) + Math.pow((arr[5][0] - arr[6][0]), 2));
        double len6_7 = Math.sqrt(Math.pow((arr[6][1] - arr[7][1]), 2) + Math.pow((arr[6][0] - arr[7][0]), 2)); //right

        //손 뻗는 모양: 팔꿈치(3번,6번), 손목(4번,7번)-> 어깨(2번,5번)과 높이가 같게
        arr[3][0] = arr[2][0];
        arr[4][0] = arr[2][0];
        arr[6][0] = arr[5][0];
        arr[7][0] = arr[5][0];

        //팔 길이 조정
        arr[3][1] = arr[2][1]-(float)len2_3;
        arr[4][1] = arr[3][1]-(float)len3_4; //left 왼쪽 측면 그려질 때
        arr[6][1] = arr[5][1]+(float)len5_6;
        arr[7][1] = arr[6][1]+(float)len6_7; // right 오른쪽 측면 그려질 때

        //발목이 무릎보다 뒤로 가게
        double len9_10_1 = Math.sqrt(Math.pow((arr[9][1] - arr[10][1]), 2) + Math.pow((arr[9][0] - arr[10][0]), 2)); // 좌표 조정 전 다리 하부 길이

        arr[10][1] = arr[10][1] + (float)0.13*(float)len8_9; //left
        arr[13][1] = arr[13][1] - (float)0.13*(float)len11_12; // right 발목 x 좌표를 무릎보다 뒤쪽으로 이동

        double len9_10_2 = Math.sqrt(Math.pow((arr[9][1] - arr[10][1]), 2) + Math.pow((arr[9][0] - arr[10][0]), 2)); // 좌표 조정 후 다리 하부 길이

        double lenleg = len9_10_2 - len9_10_1; // 좌표 조정 전후 길이 차

        arr[9][0] = arr[9][0] + (float) lenleg; // left
        arr[12][0] = arr[12][0] + (float)lenleg; // right좌표 조정으로 인한 길이 차만큼 무릎 좌표의 y축 조정

        //가이드라인 위치 조정
        double aheight = 0.5 * Math.sqrt(Math.pow((arr[9][1] - arr[10][1]), 2) + Math.pow((arr[9][0] - arr[10][0]), 2));
        float aheight2 = (float)0.5 * (arr[9][0] - arr[10][0]);
        for(int i=0; i<14; i++)
            arr[i][0] = arr[i][0] - (float)aheight2;
        // y = y - (float)aheight;
    }

    protected void wideSquatPose(){
        //높이차
        float heightRight=arr[11][0]-arr[12][0];
        float heigtLeft=arr[8][0]-arr[9][0];

        //발 너비를 어깨 너비만큼 벌어지게
        double len2_5 = Math.abs(arr[2][1]-arr[5][1]);
        arr[10][1] = arr[11][1] - (float)(0.5*len2_5);
        arr[13][1] = arr[13][1] + (float)(0.5*len2_5);

        float x, y;
        x = (arr[8][1] + arr[11][1]) / 2 + 5;

        //무릎-발목: 8번(11번)의 y위치를 9번(12번)과 같게
        double len8_9=Math.sqrt(Math.pow((arr[8][1]-arr[9][1]),2)+Math.pow((arr[8][0]-arr[9][0]),2));
        arr[8][0]=arr[9][0];
        arr[8][1]= x + (float)(0.5*len2_5);

        double len11_12=Math.sqrt(Math.pow((arr[11][1]-arr[12][1]),2)+Math.pow((arr[11][0]-arr[12][0]),2));
        arr[11][0]=arr[12][0];
        arr[11][1]=x + (float)(0.5*len2_5);

        //상체(머리,목,어깨)높이: 엉덩이 내려간 만큼 같이 내려가게
        arr[0][0]=arr[0][0]-heigtLeft;
        arr[1][0]=arr[1][0]-heigtLeft;
        arr[2][0]=arr[2][0]-heigtLeft;
        arr[5][0]=arr[5][0]-heightRight;

        //손 뻗는 모양: 팔꿈치(3번,6번), 손목(4번,7번)-> 어깨(2번,5번)과 높이가 같게
        arr[3][0]=arr[2][0];
        arr[4][0]=arr[2][0];
        arr[6][0]=arr[5][0];
        arr[7][0]=arr[5][0];

    }
    protected void lungePose(){

    }

    protected void vUpPose(float x, float y){
        double len1x = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        arr[1][0] = arr[1][0]+(float)len1x;
    }

    protected void onDraw(Canvas canvas) {
//            tt.setText("아아");

        //  startTimer();
        System.out.println("count Check, onDraw 호출");
//        canvas.drawText("토끼",250, 250, ppaint);
        count++;

//        float n= angleBetween2Lines(new PointF(arr[11][1],arr[11][0]), new PointF(arr[12][1],arr[12][0]), new PointF(arr[12][1],arr[12][0]), new PointF(arr[13][1],arr[13][0]));
        //와이드스쿼트
        //wideSquartPose();

        //스쿼트
        squatPose();

        //런지
        //lungePose();

        canvas.drawLine(arr[0][1], arr[0][0], arr[1][1], arr[1][0], ppaint);
        canvas.drawLine(arr[1][1], arr[1][0], arr[2][1], arr[2][0], ppaint);
        //canvas.drawLine(arr[1][1], arr[1][0], arr[5][1], arr[5][0], ppaint); // 스쿼트포즈 x
        canvas.drawLine(arr[2][1], arr[2][0], arr[3][1], arr[3][0], ppaint);
        canvas.drawLine(arr[3][1], arr[3][0], arr[4][1], arr[4][0], ppaint);
        //canvas.drawLine(arr[5][1], arr[5][0], arr[6][1], arr[6][0], ppaint); // 스쿼트포즈 x
        //canvas.drawLine(arr[6][1], arr[6][0], arr[7][1], arr[7][0], ppaint); // 스쿼트포즈 x

        float x, y;
        x = (arr[8][1] + arr[11][1]) / 2 + 5;
        y = (arr[8][0] + arr[11][0]) / 2 + 5; // 골반 사이 좌표

        //canvas.drawLine(arr[1][1], arr[1][0], x, y, ppaint); //스쿼트포즈 x
        //canvas.drawLine(x, y, arr[8][1], arr[8][0], ppaint); //스쿼트포즈 x
        //canvas.drawLine(x, y, arr[11][1], arr[11][0], ppaint); //스쿼트포즈 x

        canvas.drawLine(arr[1][1], arr[1][0], arr[8][1], arr[8][0], ppaint);
        //canvas.drawLine(arr[1][1], arr[1][0], arr[11][1], arr[11][0], ppaint); //스쿼트포즈 x

        canvas.drawLine(arr[8][1], arr[8][0], arr[9][1], arr[9][0], ppaint);
        canvas.drawLine(arr[9][1], arr[9][0], arr[10][1], arr[10][0], ppaint);
        //canvas.drawLine(arr[11][1], arr[11][0], arr[12][1], arr[12][0], ppaint); //스쿼트포즈 x
        //canvas.drawLine(arr[12][1], arr[12][0], arr[13][1], arr[13][0], ppaint); //스쿼트포즈 x

           /* for (int i = 0; i < 14; i++) {
//            Log.d("check1", i + " : " + arr[i][0] + " " + arr[i][1]);
                canvas.drawPoint(arr[i][1], arr[i][0], paint[i]);
            }*/

        // PoseEstimationTimer pose =new PoseEstimationTimer();
        // TextView text=findViewById(R.id.text_count);
        //pose.startTimer(this);

        Timer t_timer=new Timer();

        TimerTask t_task=new TimerTask() {
            int count=1;
            @Override
            public void run() {

                if(count==1) {
                    myView.invalidate();
                    count++;
                }
                else{
                    t_timer.cancel();
                    t_timer.purge();
                }

            }
        };
        t_timer.schedule(t_task,10000);


    }

    /*추가*/
    //float[][] standard=new float[14][2];

    /*추가 pose count함수 */
    public static int poseEstimation(int countPose){
        return countPose;
    }

    public static void setArr(float[][] inputArr) {

        for (int index = 0; index < NUMJOINT; index++) {
            arr[index][0] = inputArr[index][0] / HEATMAPHEIGHT * MAX_PREVIEW_HEIGHT;
            arr[index][1] = inputArr[index][1] / HEATMAPWIDTH * MAX_PREVIEW_WIDTH;
        }

        /*Log.d("count Check", "count: " + count + ", count1: " + count1++);
        Log.d("line", "--------------");
        for (int i = 0; i < NUMJOINT; i++) {
            Log.d("check", arr[i][0] + " " + arr[i][1]);
        }
        Log.d("line", "--------------");*/


    }

}