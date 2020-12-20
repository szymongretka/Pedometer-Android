package pl.paum.pedometer.handler.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.paum.pedometer.handler.DataHandler;

public class DataHandlerImpl implements DataHandler {

    private Context applicationContext;
    private int currentHour;
    private String mapKey;

    public DataHandlerImpl(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void saveToMemory(int numOfSteps) {
        currentHour = Calendar.HOUR_OF_DAY;
        Map<String, Integer> inputMap = new HashMap<>();
        inputMap.put(String.valueOf(currentHour), numOfSteps);
        saveMap(inputMap);
        Toast.makeText(applicationContext, "Saved Locally!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exportDataToCsv() {

        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Time,Distance");
        for (int i = 0; i < 5; i++) {
            data.append("\n" + String.valueOf(i) + "," + String.valueOf(i * i));
        }

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

    private void saveMap(Map<String, Integer> inputMap) {
        mapKey = Calendar.getInstance().getTime().toString();
        SharedPreferences pSharedPref = applicationContext
                .getSharedPreferences("MyDailyReport", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            if (pSharedPref.contains(mapKey)) {
                editor.putString(mapKey, jsonString); //TODO
            } else {
                editor.putString(mapKey, jsonString);
            }
            editor.apply();
        }
    }

}
