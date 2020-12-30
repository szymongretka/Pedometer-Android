package pl.paum.pedometer.handler.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.paum.pedometer.handler.DataHandler;

public class DataHandlerImpl implements DataHandler {

    private Context applicationContext;

    public DataHandlerImpl(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void saveToMemory(int numOfSteps) {
        LocalDateTime currentTime = LocalDateTime.now();
        SharedPreferences pSharedPref = applicationContext
                .getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.putString(currentTime.toString(), String.valueOf(numOfSteps));
            editor.apply();
        }
    }

    @Override
    public void exportDataToCsv() {
        SharedPreferences pSharedPref = applicationContext
                .getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        Map<String, ?> sharedPrefMap = pSharedPref.getAll();
        //generate data
        StringBuilder data = new StringBuilder();

        data.append("Time/Date, ");

        for (int i = 0; i < 24; i++) {
            String hour = String.valueOf(i);
            if (i < 10) {
                hour = "0".concat(String.valueOf(i));
            }
            for (int j = 0; j < 60; j++) {
                String minute = String.valueOf(j);
                if (j < 10) {
                    minute = "0".concat(String.valueOf(j));
                }
                data.append(hour.concat(":").concat(minute).concat(", "));
            }
        }

        Map<String, String> sortedMap = new LinkedHashMap<>();

        sharedPrefMap.forEach((key, value) -> {
            String dateKey = key.substring(0, key.indexOf('T'));
            if (!sortedMap.containsKey(dateKey)) {
                sortedMap.put(dateKey, ", ".concat(String.valueOf(value)));
            } else {
                sortedMap.computeIfPresent(dateKey, (k, v) -> v.concat(", ").concat(String.valueOf(value)));
            }
        });

        sortedMap.forEach((key, value) -> {
            data.append("\n".concat(key).concat(value));
        });

        try {
            //saving the file into device
            FileOutputStream out = applicationContext.openFileOutput("data.csv",
                    Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();

            //exporting
            File filelocation = new File(applicationContext.getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(applicationContext,
                    "pl.paum.pedometer.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);

            Intent chooser = Intent.createChooser(fileIntent, "Share File");
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            List<ResolveInfo> resInfoList = applicationContext.getPackageManager()
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                applicationContext.grantUriPermission(
                        packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            applicationContext.startActivity(chooser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
