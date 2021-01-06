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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import pl.paum.pedometer.handler.DataHandler;
import pl.paum.pedometer.util.AppSharedCtx;

public class DataHandlerImpl implements DataHandler {

    private Context applicationContext;
    private SharedPreferences pSharedPref;
    private Map<String, ?> sharedPrefMap;

    private int interval = AppSharedCtx.POLL_PERIOD_SEC_EXPORT/60;

    public DataHandlerImpl(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.pSharedPref = applicationContext
                .getSharedPreferences("StepCounter", Context.MODE_PRIVATE);
        this.sharedPrefMap = pSharedPref.getAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void saveToMemory(int numOfSteps) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (pSharedPref != null) {
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.putString(currentTime.toString(), String.valueOf(numOfSteps));
            editor.apply();
        }
    }

    @Override
    public void exportDataToCsv() {
        //generate data
        StringBuilder data = new StringBuilder();

        int samples = (int)(((float)60/AppSharedCtx.POLL_PERIOD_SEC_EXPORT)*60);

        data.append("Time/Date, ");

        for (int i = 0; i < 24; i++) {
            String hour = String.valueOf(i);
            if (i < 10) {
                hour = "0".concat(String.valueOf(i));
            }
            for (int j = 0; j < samples; j++) {
                String minute = String.valueOf(j*interval);
                if (j*interval < 10) {
                    minute = "0".concat(minute);
                }
                data.append(hour.concat(":").concat(minute).concat(", "));
            }
        }

        Map<LocalDate, LinkedList<LocalTime>> dateTimeMap = new HashMap<>();

        sharedPrefMap.forEach((key, value) -> {
            LocalDate dateKey = LocalDateTime.parse(key).toLocalDate();
            dateTimeMap.computeIfAbsent(dateKey,
                    k -> new LinkedList<>()).add(LocalDateTime.parse(key).toLocalTime());
        });

        Map<String, String> sortedMap = generateValuesMap(dateTimeMap);

        sortedMap.forEach((key, value) -> data.append("\n".concat(key).concat(value)));

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

    @Override
    public Integer getDailyNumOfSteps() {
        AtomicInteger sum = new AtomicInteger(0);
        LocalDate currentDate = LocalDateTime.now().toLocalDate();

        sharedPrefMap.forEach((key, value) -> {
            if (key.substring(0, key.indexOf('T')).equals(currentDate.toString())) {
                sum.addAndGet(Integer.parseInt((String) value));
            }
        });

        return sum.get();
    }

    private Map<String, String> generateValuesMap(Map<LocalDate, LinkedList<LocalTime>> dateTimeMap) {
        Map<String, String> map = new LinkedHashMap<>();

        int numOfRecords = (int)(24 * 60 * ((float)60 / AppSharedCtx.POLL_PERIOD_SEC_EXPORT));

        for (LocalDate key : dateTimeMap.keySet()) {
            String dateKey = key.toString();
            map.put(dateKey, "");
            LinkedList<LocalTime> valueList = dateTimeMap.get(key)
                    .stream()
                    .sorted()
                    .collect(Collectors.toCollection(LinkedList::new));

            int minuteOfTheDay = valueList.getFirst().withSecond(0).withNano(0).toSecondOfDay() / 60;

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 1; i <= numOfRecords; i++) {

                int steps = 0;
                while (!valueList.isEmpty() && i*interval > minuteOfTheDay) {
                    String concatedKeyString = dateKey.concat("T").concat(valueList.getFirst().toString());
                    String value = (String) sharedPrefMap
                            .get(concatedKeyString);
                    steps += Integer.parseInt(value);
                    valueList.removeFirst();
                    if (!valueList.isEmpty()) {
                        minuteOfTheDay = valueList.getFirst().withSecond(0).withNano(0).toSecondOfDay() / 60;
                    }
                }
                stringBuilder.append(", ").append(steps);

            }

            map.put(dateKey, stringBuilder.toString());
        }

        return map;
    }

}
