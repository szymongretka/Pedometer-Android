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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import pl.paum.pedometer.util.AppSharedCtx;

import static pl.paum.pedometer.util.AppSharedCtx.NUM_OF_STEPS;
import static pl.paum.pedometer.util.AppSharedCtx.POLL_PERIOD_SEC;
import static pl.paum.pedometer.util.AppSharedCtx.PREVIOUS_NUM_OF_STEPS;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private AppSharedCtx appSharedCtx;
    private String dailyText;

    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;
    private ScheduledFuture<?> stepCounterResetTask;
    private DataHandler dataHandler;

    private TextView TvSteps;

    private Button BtnStart;
    private Button BtnStop;
    private Button BtnExport;
    private Button BtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSharedCtx = new AppSharedCtx((SensorManager) getSystemService(SENSOR_SERVICE));

        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        dataHandler = new DataHandlerImpl(this);
        ButtonActionsHandler buttonActionsHandler = new ButtonActionsHandlerImpl(this);

        NUM_OF_STEPS = dataHandler.getDailyNumOfSteps();
        PREVIOUS_NUM_OF_STEPS = NUM_OF_STEPS;

        TvSteps = findViewById(R.id.tv_steps);
        BtnStart = findViewById(R.id.btn_start);
        BtnStop = findViewById(R.id.btn_stop);
        BtnExport = findViewById(R.id.btn_export);
        BtnExit = findViewById(R.id.btn_exit);

        BtnStart.setOnClickListener((View v) -> buttonActionsHandler.startButtonAction());
        BtnStop.setOnClickListener((View v) -> buttonActionsHandler.stopButtonAction());
        BtnExit.setOnClickListener((View v) -> buttonActionsHandler.exitButtonAction());
        BtnExport.setOnClickListener((View v) -> dataHandler.exportDataToCsv());
        scheduleResetAtMidnight();
        dailyText = getResources().getString(R.string.daily_text).concat("\n").concat("\n");
        TvSteps.setText(dailyText.concat(String.valueOf(NUM_OF_STEPS)));
        scheduledTask = scheduledExecutor.scheduleAtFixedRate(() -> {
            int stepDiff = NUM_OF_STEPS - PREVIOUS_NUM_OF_STEPS;
            PREVIOUS_NUM_OF_STEPS = NUM_OF_STEPS;
            dataHandler.saveToMemory(stepDiff);
        }, 0, POLL_PERIOD_SEC, TimeUnit.SECONDS);
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
        scheduledTask.cancel(false);
    }

    @Override
    public void step(long timeNs) {
        NUM_OF_STEPS++;
        TvSteps.setText(dailyText.concat(String.valueOf(NUM_OF_STEPS)));
    }

    public AppSharedCtx getAppSharedCtx() {
        return appSharedCtx;
    }

    private long calculateSecondsTo(int targetHour, int targetMinute, int targetSecond){
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(),currentZone);
        ZonedDateTime zonedTarget = zonedNow.withHour(targetHour).withMinute(targetMinute).withSecond(targetSecond);
        if(zonedNow.compareTo(zonedTarget) > 0){
            zonedTarget = zonedTarget.plusDays(1);
        }
        return Duration.between(zonedNow, zonedTarget).getSeconds();
    }

    private void scheduleResetAtMidnight(){
        stepCounterResetTask = scheduledExecutor.scheduleAtFixedRate(() -> {
            int stepDiff = NUM_OF_STEPS - PREVIOUS_NUM_OF_STEPS;
            PREVIOUS_NUM_OF_STEPS = NUM_OF_STEPS;
            dataHandler.saveToMemory(stepDiff);

            //RESET STEPS
            PREVIOUS_NUM_OF_STEPS = 0;
            NUM_OF_STEPS = 0;
        }, calculateSecondsTo(23,59,59), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
}
