package pl.paum.pedometer.handler.impl;

import android.app.AlertDialog;
import android.widget.Toast;

import pl.paum.pedometer.MainActivity;
import pl.paum.pedometer.handler.ButtonActionsHandler;

import static pl.paum.pedometer.MainActivity.NUM_OF_STEPS;


public class ButtonActionsHandlerImpl implements ButtonActionsHandler {

    private final MainActivity mainActivity;

    public ButtonActionsHandlerImpl(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void exitButtonAction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit ??");
        builder.setPositiveButton("Yes. Exit now!", (dialog, which) -> {
            mainActivity.finish();
            System.exit(0);
        });

        builder.setNegativeButton("Not now", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void startButtonAction() {
        NUM_OF_STEPS = 0;
        Toast.makeText(mainActivity, "Started!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopButtonAction() {
        System.out.println("NUM OF STEPS!!!!!!!!!!!!!!!!!!!: " + NUM_OF_STEPS);
        Toast.makeText(mainActivity, "Stopped!", Toast.LENGTH_SHORT).show();
    }


}
