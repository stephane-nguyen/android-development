package com.tps.opengl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyView extends SurfaceView implements Runnable, View.OnTouchListener, SensorEventListener {
    private static final float BALL_SPEED_SCALE = 1f;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Thread thread = null;
    private SurfaceHolder sh;
    private boolean paused = true;
    private List<Circle> circles = new ArrayList<>();

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accelerometerX, accelerometerY;

    public MyView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        sh = getHolder();
        setOnTouchListener(this);

        // Initialize sensor variables
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public MyView(Context context) {
        super(context, null);
        sh = getHolder();
        setOnTouchListener(this);

        // Initialize sensor variables
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void resume() { // to call in Activity.onResume
        paused = false;
        thread = new Thread(this);
        thread.start();

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pause() {// to call in Activity.onPause
        paused = true;
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;

        sensorManager.unregisterListener(this);
    }

    public void run() {
        while (!paused) {
            if (!sh.getSurface().isValid()) continue;
            Canvas c = sh.lockCanvas();

            // Clear the canvas with a white background
            c.drawColor(Color.WHITE);

            for (Circle circle : circles) {
                circle.move(accelerometerX, accelerometerY);
                // Ensure the circle stays within the screen boundaries
                circle.setX(Math.max(circle.getRadius(), Math.min(c.getWidth() - circle.getRadius(), circle.getX())));
                circle.setY(Math.max(circle.getRadius(), Math.min(c.getHeight() - circle.getRadius(), circle.getY())));

                // Take account of collision if button is clicked
                // Check for collisions with other circles
                for (Circle otherCircle : circles) {
                    if (circle != otherCircle) {
                        if (circle.collidesWith(otherCircle)) {
                            //
                        }
                    }
                }

                drawCircle(c, circle);
            }

            sh.unlockCanvasAndPost(c);
        }
    }

    private void drawCircle(Canvas canvas, Circle circle) {
        paint.setColor(circle.getColor());
        canvas.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int color = generateRandomColor();
            int radius = generateRandomValue();
            circles.add(new Circle(x, y, color, radius));
        }
        return true;
    }

    public static int generateRandomColor() {
        Random random = new Random();
        int red = 30 + random.nextInt(200);
        int green = 30 + random.nextInt(200);
        int blue = 30 + random.nextInt(200);
        return Color.rgb(red, green, blue);
    }

    public static int generateRandomValue() {
        Random random = new Random();
        // Generate a random value between 20 and 40
        return random.nextInt(21) + 20;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerX = x * BALL_SPEED_SCALE;
            accelerometerY = -y * BALL_SPEED_SCALE;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}