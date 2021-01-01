package pl.paum.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import pl.paum.pedometer.detector.StepDetector;
import pl.paum.pedometer.handler.ButtonActionsHandler;
import pl.paum.pedometer.handler.DataHandler;
import pl.paum.pedometer.handler.impl.ButtonActionsHandlerImpl;
import pl.paum.pedometer.handler.impl.DataHandlerImpl;
import pl.paum.pedometer.listener.StepListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";

    private final static int POLL_PERIOD_SEC = 60;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;

    private TextView TvSteps;

    public final static int ACCELEROMETER_EVENTS_SAMPLING_PERIOD = 500_000;
    public static int NUM_OF_STEPS = 0;
    public static int PREVIOUS_NUM_OF_STEPS = 0;

    private Button BtnStart;
    private Button BtnStop;
    private Button BtnExport;
    private Button BtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        DataHandler dataHandler = new DataHandlerImpl(this);
        ButtonActionsHandler buttonActionsHandler = new ButtonActionsHandlerImpl(this);

        scheduledTask = scheduledExecutor.scheduleAtFixedRate(() -> {
            int stepDiff = NUM_OF_STEPS - PREVIOUS_NUM_OF_STEPS;
            PREVIOUS_NUM_OF_STEPS = NUM_OF_STEPS;
            dataHandler.saveToMemory(stepDiff);
        }, 0, POLL_PERIOD_SEC, TimeUnit.SECONDS);

        TvSteps = findViewById(R.id.tv_steps);
        BtnStart = findViewById(R.id.btn_start);
        BtnStop = findViewById(R.id.btn_stop);
        BtnExport = findViewById(R.id.btn_export);
        BtnExit = findViewById(R.id.btn_exit);

        BtnStart.setOnClickListener((View v) -> {
            sensorManager.registerListener(MainActivity.this, accel, ACCELEROMETER_EVENTS_SAMPLING_PERIOD);
            buttonActionsHandler.startButtonAction();
        });
        BtnStop.setOnClickListener((View v) -> {
            sensorManager.unregisterListener(MainActivity.this);
            buttonActionsHandler.stopButtonAction();
        });
        BtnExport.setOnClickListener((View v) -> dataHandler.exportDataToCsv());
        BtnExit.setOnClickListener((View v) -> buttonActionsHandler.exitButtonAction());

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
        TvSteps.setText(TEXT_NUM_STEPS.concat(String.valueOf(NUM_OF_STEPS)));
    }

}
