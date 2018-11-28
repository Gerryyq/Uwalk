package com.example.android.app_hci;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;



public class WalkingActivity extends Activity implements SensorEventListener
{
    /** Called when the activity is first created. */
    // Intialising variables
    public static int x;
    public static int y;
    public int sensorX, sensorY;
    public ImageView iv;
    public Path path;
    private SensorManager sensorManager = null;
    private TextView ShowText;
    private ProgressBar pb;
    private int progressBarValue;
    int iCurStep = 0;// current step
    public long start;
    public Vibrator vibe;
    public MediaPlayer ring;
    public boolean canPlayTrack2, canPlayTrack3,  canPlayTrack4, isPlayingTrack1, isPlayingTrack2, isPlayingTrack3, isPlayingTrack4;
    public boolean hasVibrated1, hasVibrated2, hasVibrated3;
    public float m,n;

    private AnimatedView mAnimatedView = null;

    //Setting listener for bottom menu, and transition between activities
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(WalkingActivity.this.getApplication(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
                case R.id.navigation_viewStory:
                    Intent intent2 = new Intent(WalkingActivity.this.getApplication(), ViewStoryActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;

//                case R.id.navigation_storylist:
//                    mTextMessage.setText(R.string.title_storylist);
//                    return true;
                case R.id.navigation_stars:
                    Intent intent4 = new Intent(WalkingActivity.this.getApplication(), AchievementActivity.class);
                    startActivity(intent4);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
            }
            return false;
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        start = System.currentTimeMillis();
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        canPlayTrack2 = canPlayTrack3 = canPlayTrack4 = isPlayingTrack1 = isPlayingTrack2 = isPlayingTrack3 = isPlayingTrack4 = false;
        iv = (ImageView) findViewById(R.id.imageView2);
        pb = findViewById(R.id.progressBar2);
        ShowText = findViewById(R.id.text_progressbar2);
        progressBarValue = 30;
        hasVibrated1 = hasVibrated2 = hasVibrated3 = false;

//        TextView tv1 = (TextView) findViewById(R.id.button1);
//        tv1.setDrawingCacheEnabled(true);

        //Setting a fix value to progress bar
        pb.setProgress(progressBarValue);
        ShowText.setText(progressBarValue +"/"+pb.getMax());

        //Initialising bottom menu
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Check if accelerometer exist
        if(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() !=0){
            // Set up sensor
            Sensor s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //Setting first audio to be played
        ring= MediaPlayer.create(WalkingActivity.this,R.raw.track01);

        x = y = sensorX = sensorY = 0;

        mAnimatedView = new AnimatedView(this);

        //Set our drawing content to a view, not like the traditional setting to a layout
        setContentView(mAnimatedView);

    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAnimatedView.onSensorEvent(sensorEvent);
        }
    }

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop()
    {
        // Unregister the listener
        sensorManager.unregisterListener(this);

        super.onStop();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        ring.stop();
        finish();
    }

    //Convert drawable into bitmap, so it can be drawn on canvas
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    //Class to draw on the animated map
    public class AnimatedView extends View {

        private static final int CIRCLE_RADIUS = 40; //pixels

        private Paint mPaint;
        private int x;
        private int y;
        private int viewWidth;
        private int viewHeight;
        private int a,b,c;
        long elapsedTimeMillis;

        public AnimatedView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setColor(Color.parseColor("#008080"));

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            viewWidth = w;
            viewHeight = h;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            m = event.getX();
            n = event.getY();

            //Plays audio if users touch on selected area, given correct condition
            if (120 <= m  && m <=170 && 1340<= n && n <= 1380)
            {
                canPlayTrack2 = true;
                ring.stop();
            }
            if (290 <= m && m <= 360 && 830 <= n && n <= 890)
            {
                canPlayTrack3 = true;
            }
            if (600 <= m && m <= 660 &&  440 <= n && n <= 490)
            {
                canPlayTrack4 = true;
            }

            return true;
        }


        public void onSensorEvent (SensorEvent event) {
            x = x - (int) event.values[0];
            y = y + (int) event.values[1];
            //Make sure we do not draw outside the bounds of the view.
            //So the max values we can draw to are the bounds + the size of the circle
            if (x <= 0 + CIRCLE_RADIUS) {
                x = 0 + CIRCLE_RADIUS;
            }
            if (x >= viewWidth - CIRCLE_RADIUS) {
                x = viewWidth - CIRCLE_RADIUS;
            }
            if (y <= 0 + CIRCLE_RADIUS) {
                y = 0 + CIRCLE_RADIUS;
            }
            if (y >= viewHeight - CIRCLE_RADIUS) {
                y = viewHeight - CIRCLE_RADIUS;
            }
        }

        //Draws line and circle on canvas
        @Override
        protected void onDraw(Canvas canvas) {
            // a,b,c are INT referring to the number of minutes left to walk before unlock next audio clip
            a =5;
            b=10;
            c=15;
            Resources res = getResources();

            if (ring.isPlaying() == false && isPlayingTrack1 == false) {
                playAudio(R.raw.track01);
                isPlayingTrack1 = true;
            }

            Bitmap map = BitmapFactory.decodeResource(res, R.drawable.sample);
            canvas.drawBitmap(map,0,0,null);

            // Draw Path
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#794044"));
            paint.setStrokeWidth(20);

            path = new Path();
            path.moveTo(110,1700);
            path.cubicTo(500,800,550,400,850, 550);

            canvas.drawPath(path, paint);

            Paint paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(Color.parseColor("#133337"));
            RectF oval1 = new RectF(100, 1300, 200,1400);
            RectF oval2 = new RectF(300, 800, 400,900);
            RectF oval3 = new RectF(600, 400, 700,500);

            // Change color when time is up
            Paint paint2 = new Paint();
            paint2.setStyle(Paint.Style.FILL);
            paint2.setColor(Color.parseColor("#ffa500"));

            Paint p1 = new Paint();
            p1.setColor(Color.WHITE);
            p1.setTextSize(40);

            //Draws three circles
            canvas.drawOval(oval1, paint1);
            canvas.drawOval(oval2, paint1);
            canvas.drawOval(oval3, paint1);

            //Counting the minutes
            elapsedTimeMillis = System.currentTimeMillis() - start;
            if (elapsedTimeMillis-60000 > 0)
            {
                long minutes = (elapsedTimeMillis/60000);

                a = a - (int)minutes;
                b = b - (int)(minutes);
                c = c - (int)(minutes);
            }
            if (a <=0)
            {
                a = 0;
                //Changing color of circle
                canvas.drawOval(oval1, paint2);
                vibe.cancel();
            }
            if (b<=0)
            {
                b=0;
                canvas.drawOval(oval2, paint2);
            }
            if (c<=0)
            {
                c=0;
                canvas.drawOval(oval3, paint2);
            }
            if (a ==0 && canPlayTrack2 == true)
            {
                if (ring.isPlaying() == true)
                {
                    ring.stop();
                }
                playAudio(R.raw.track02);
                isPlayingTrack2 = true;
                canPlayTrack2 = false;
            }
            if (b ==0 && canPlayTrack3 == true)
            {
                if (ring.isPlaying() == true) {
                    ring.stop();
                }
                playAudio(R.raw.track03);
                isPlayingTrack3 = true;
                canPlayTrack3 = false;

            }
            if (c ==0 && canPlayTrack4== true)
            {
                if (ring.isPlaying() == true) {
                    ring.stop();
                }
                playAudio(R.raw.track04);
                isPlayingTrack4 = true;
                canPlayTrack4 = false;
            }
            if(a==0 && hasVibrated1==false)
            {
                vibe.vibrate(1000);
                hasVibrated1 = true;
                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    // Do nothing
                }

            }
            if(b==0 && hasVibrated2==false)
            {
                vibe.vibrate(1000);
                hasVibrated2 = true;
                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    // Do nothing
                }
            }

            if(c==0 && hasVibrated3==false)
            {
                vibe.vibrate(1000);
                hasVibrated3 = true;
                try{
                    Thread.sleep(1000);
                }
                catch(InterruptedException e) {
                    // Do nothing
                }
            }

            canvas.drawText(Integer.toString(a), 140, 1360, p1);
            canvas.drawText(Integer.toString(b), 325, 860, p1);
            canvas.drawText(Integer.toString(c), 630, 460, p1);

            Bitmap firstButton = getBitmap(R.drawable.round_button);
//            canvas.drawBitmap(firstButton,15 + x,1550 + y,null);

            Matrix mxTransform = new Matrix();
            PathMeasure pm = new PathMeasure(path,false);
            float fSegmentLen = pm.getLength() / 5000;

            if (iCurStep <= 5000) {

                pm.getMatrix(fSegmentLen * iCurStep, mxTransform, PathMeasure.POSITION_MATRIX_FLAG);
                canvas.drawBitmap(firstButton, mxTransform, null);

                iCurStep++;

                //We need to call invalidate each time, so that the view continuously draws
                invalidate();

            } else {
                iCurStep = 0;
            }

        }

        public void playAudio(int resource)
        {
            ring= MediaPlayer.create(WalkingActivity.this,resource);
            ring.start();
        }
    }
}