package pl.paum.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    private static int NUM_OF_STEPS = 0;
    private static int PREVIOUS_NUM_OF_STEPS = 0;

    private final static int POLL_PERIOD_SEC = 60;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;

    private TextView TvSteps;

    private Button BtnStart;
    private Button BtnStop;
    private Button BtnExport;
    private Button BtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataHandler dataHandler = new DataHandlerImpl(MainActivity.this);

        scheduledTask = scheduledExecutor.scheduleAtFixedRate(() -> {
            int stepDiff = NUM_OF_STEPS - PREVIOUS_NUM_OF_STEPS;
            PREVIOUS_NUM_OF_STEPS = NUM_OF_STEPS;
            dataHandler.saveToMemory(stepDiff);
        }, 0, POLL_PERIOD_SEC, TimeUnit.SECONDS);

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


        BtnStart.setOnClickListener((View v) -> {
            NUM_OF_STEPS = 0;
            sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            Toast.makeText(this, "Started!", Toast.LENGTH_SHORT).show();
        });


        BtnStop.setOnClickListener((View v) -> {
                    sensorManager.unregisterListener(MainActivity.this);
                    Toast.makeText(this, "Stopped!", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        //TODO: save collection to file
        scheduledTask.cancel(false);
    }

    @Override
    public void step(long timeNs) {
        NUM_OF_STEPS++;
        TvSteps.setText(TEXT_NUM_STEPS + NUM_OF_STEPS);
    }

}
