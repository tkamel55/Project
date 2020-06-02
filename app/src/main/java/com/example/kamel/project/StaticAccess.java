package com.example.kamel.project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class StaticAccess {
    /// TABLE NAMES
    public static final String USERS_TABLE = "Admin";


    public static String getDateTimeNow() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

    }

    public static String parseCreatedDate(String time) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time);
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            return "invalid date";
        }
    }

    public static String getMiniFromStartEndTime(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            long hour = TimeUnit.MILLISECONDS.toHours(endDate.getTime() - startDate.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - startDate.getTime());
            long second = TimeUnit.MILLISECONDS.toSeconds(endDate.getTime() - startDate.getTime());
            return String.valueOf(hour+":"+minutes+":"+second+" sec");
        } catch (ParseException e) {
            return "00:00:00 sec";
        }

    }
}
