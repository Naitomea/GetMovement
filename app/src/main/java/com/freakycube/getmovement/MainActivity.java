package com.freakycube.getmovement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Vector;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    TextView valuesAccelView, valuesLinearAccelView;
    TextView nanoTimeElapsedView;
    TextView metersView;

    SensorManager sensorMgr;
    Sensor accelerometer, linearAccelerometer;

    float meters = 0;
    long lastNanoTime = 0, newNanoTime = 0, nanoTimeElapsed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valuesAccelView = (TextView) findViewById(R.id.valuesAccel);
        valuesLinearAccelView = (TextView) findViewById(R.id.valuesLinearAccel);
        nanoTimeElapsedView = (TextView) findViewById(R.id.nanoTimeElapsed);
        metersView = (TextView) findViewById(R.id.meters);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            valuesAccelView.setText("z = " + String.valueOf(event.values[2]));
        }else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            valuesLinearAccelView.setText("z = " + String.valueOf(event.values[2]));

            newNanoTime = System.nanoTime();
            nanoTimeElapsed = newNanoTime - lastNanoTime;
            lastNanoTime = newNanoTime;

            nanoTimeElapsedView.setText("NanoTime Elapsed = " + String.valueOf(nanoTimeElapsed));

            meters += event.values[2] * (nanoTimeElapsed/1000000000);
            metersView.setText("Meters = " + String.valueOf(meters));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        sensorMgr.unregisterListener(this, accelerometer);
        //sensorManager.unregisterListener(this, gravity);
        sensorMgr.unregisterListener(this, linearAccelerometer);
        super.onPause();
    }
    /* * (non-Javadoc) *  * @see android.app.Activity#onResume() */
    @Override
    protected void onResume() {
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        //sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_UI);
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
