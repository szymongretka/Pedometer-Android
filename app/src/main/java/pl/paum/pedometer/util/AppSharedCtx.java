package pl.paum.pedometer.util;

import android.hardware.SensorManager;

public class AppSharedCtx extends AppSharedSensorMgmt{

    public final static int ACCELEROMETER_EVENTS_SAMPLING_PERIOD = 500_000;
    public static int NUM_OF_STEPS;
    public static int PREVIOUS_NUM_OF_STEPS;

    public AppSharedCtx(SensorManager sensorManager) {
        super(sensorManager);
    }

}
