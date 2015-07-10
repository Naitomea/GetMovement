package com.freakycube.getmovement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Vector;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    String TAG = "MainActivity";

    TextView valuesAccelView, valuesLinearAccelView;
    TextView nanoTimeElapsedView;
    TextView metersView, speedView, gravityView;

    Button calibrateButton;
    Vector calibrateData = new Vector();
    double offsetCalibrage = 0.f;
    long lastCalibrageNanoTime = 0;

    SensorManager sensorMgr;
    Sensor accelerometer, linearAccelerometer, gravity;

    float meters = 0, speed = 0;
    long lastNanoTime = 0, newNanoTime = 0, nanoTimeElapsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valuesAccelView = (TextView) findViewById(R.id.valuesAccel);
        valuesLinearAccelView = (TextView) findViewById(R.id.valuesLinearAccel);
        nanoTimeElapsedView = (TextView) findViewById(R.id.nanoTimeElapsed);
        metersView = (TextView) findViewById(R.id.meters);
        speedView = (TextView) findViewById(R.id.speed);
        gravityView = (TextView) findViewById(R.id.gravity);

        calibrateButton = (Button) findViewById(R.id.calibrateButton);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCalibrageNanoTime = System.nanoTime();
            }
        });

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityView.setText("Gravity = " + String.valueOf(event.values[2]) + " m/s�");
        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            if (lastCalibrageNanoTime == 0) {
                valuesAccelView.setText("z = " + String.valueOf(event.values[2]));

                newNanoTime = System.nanoTime();
                nanoTimeElapsed = newNanoTime - lastNanoTime;
                lastNanoTime = newNanoTime;

                nanoTimeElapsedView.setText("NanoTime Elapsed = " + String.valueOf(nanoTimeElapsed));

                /***************************/
                speed += ((event.values[2] - 9.80665) - offsetCalibrage);
                speedView.setText("Speed = " + String.valueOf(speed) + " m/s");
                /***************************/

                meters += event.values[2] * (nanoTimeElapsed / 1000000000);
                metersView.setText("Meters = " + String.valueOf(meters));
            }
            else {
                if ((System.nanoTime() - lastCalibrageNanoTime) < 2000000000) {
                    calibrateData.add(event.values[2] - 9.80665);
                } else {
                    lastCalibrageNanoTime = 0;

                    Iterator i = calibrateData.iterator();
                    while (i.hasNext())
                        offsetCalibrage += (double) i.next();
                    if (calibrateData.size() != 0) offsetCalibrage /= (double) calibrateData.size();

                    Log.i(TAG, String.valueOf(offsetCalibrage));
                }
            }

            
            gravityView.setText("Gravity = 9.80665 m/s²");
        }else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        sensorMgr.unregisterListener(this, accelerometer);
        if (gravity != null)
            sensorMgr.unregisterListener(this, gravity);
        if (linearAccelerometer != null)
            sensorMgr.unregisterListener(this, linearAccelerometer);
        super.onPause();
    }
    /* * (non-Javadoc) *  * @see android.app.Activity#onResume() */
    @Override
    protected void onResume() {
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (gravity != null)
            sensorMgr.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
        if (linearAccelerometer != null)
            sensorMgr.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
