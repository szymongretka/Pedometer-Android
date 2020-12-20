package pl.paum.pedometer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import pl.paum.pedometer.detector.StepDetector;
import pl.paum.pedometer.handler.DataHandler;
import pl.paum.pedometer.handler.impl.DataHandlerImpl;
import pl.paum.pedometer.listener.StepListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    private TextView TvSteps;

    private Button BtnStart;
    private Button BtnStop;
    private Button BtnSave;
    private Button BtnExport;
    private Button BtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataHandler dataHandler = new DataHandlerImpl(MainActivity.this);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = findViewById(R.id.tv_steps);
        BtnStart = findViewById(R.id.btn_start);
        BtnStop = findViewById(R.id.btn_stop);
        BtnExport = findViewById(R.id.btn_export);
        BtnExit = findViewById(R.id.btn_exit);
        BtnSave = findViewById(R.id.btn_save);


        BtnStart.setOnClickListener((View v) -> {
            numSteps = 0;
            sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        });


        BtnStop.setOnClickListener((View v) ->
                sensorManager.unregisterListener(MainActivity.this)
        );

        BtnSave.setOnClickListener((View v) -> {
                    dataHandler.saveToMemory(numSteps);
                }
        );

        BtnExport.setOnClickListener((View v) -> {
                    dataHandler.exportDataToCsv();
                }
        );

        BtnExit.setOnClickListener((View v) -> {
                    finish();
                    System.exit(0);
                }
        );


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }

}
