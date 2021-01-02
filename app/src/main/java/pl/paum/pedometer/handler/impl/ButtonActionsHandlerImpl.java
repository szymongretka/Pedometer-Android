package pl.paum.pedometer.handler.impl;

import android.app.AlertDialog;
import android.hardware.SensorManager;
import android.widget.Toast;

import pl.paum.pedometer.MainActivity;
import pl.paum.pedometer.R;
import pl.paum.pedometer.handler.ButtonActionsHandler;

import static pl.paum.pedometer.util.AppSharedCtx.ACCELEROMETER_EVENTS_SAMPLING_PERIOD;


public class ButtonActionsHandlerImpl implements ButtonActionsHandler {

    private final MainActivity mainActivity;

    public ButtonActionsHandlerImpl(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void exitButtonAction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(mainActivity.getResources().getString(R.string.exit));
        builder.setMessage(mainActivity.getResources().getString(R.string.exit_message));
        builder.setPositiveButton(mainActivity.getResources().getString(R.string.exit_confirmation), (dialog, which) -> {
            mainActivity.finish();
            System.exit(0);
        });

        builder.setNegativeButton(mainActivity.getResources().getString(R.string.exit_deny), (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void startButtonAction() {
        getSensorManager().registerListener(mainActivity, mainActivity.getAppSharedCtx().getAccel(),
                ACCELEROMETER_EVENTS_SAMPLING_PERIOD);
        Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.toast_started), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopButtonAction() {
        getSensorManager().unregisterListener(mainActivity);
        Toast.makeText(mainActivity, "Stopped!", Toast.LENGTH_SHORT).show();
    }

    private SensorManager getSensorManager() {
        return mainActivity.getAppSharedCtx().getSensorManager();
    }

}
