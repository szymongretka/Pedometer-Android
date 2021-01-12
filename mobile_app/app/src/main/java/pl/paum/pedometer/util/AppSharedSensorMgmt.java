package pl.paum.pedometer.util;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AppSharedSensorMgmt {

    private SensorManager sensorManager;
    private Sensor accel;

    public AppSharedSensorMgmt(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public Sensor getAccel() {
        return accel;
    }

    public void setAccel(Sensor accel) {
        this.accel = accel;
    }
}
