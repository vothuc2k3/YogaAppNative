package com.example.universalyoga.utils;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static Timestamp convertStringToTimestamp(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            Date date = dateFormat.parse(strDate);

            Timestamp timestamp = new Timestamp(date);

            System.out.println("Timestamp: " + timestamp);
            return timestamp;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static Time convertToTime(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timeString);
            return new Time(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
