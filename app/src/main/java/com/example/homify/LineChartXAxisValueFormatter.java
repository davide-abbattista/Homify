package com.example.homify;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = ((long) value) * 1000;

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        String pattern="dd-MM-yyyy HH:mm";
        DateFormat dateTimeFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());


        System.out.println(dateTimeFormat.format(timeMilliseconds));
        return dateTimeFormat.format(timeMilliseconds);
    }

}
