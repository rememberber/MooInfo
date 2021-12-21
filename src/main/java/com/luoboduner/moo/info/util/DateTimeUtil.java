package com.luoboduner.moo.info.util;

public class DateTimeUtil {

    public static String toReadableTime(long seconds) {
        String readableTime;
        int hours = (int) (seconds / 3600);
        int minutes = (int) (seconds % 3600 / 60);
        readableTime = String.format("%dh:%02dmin", hours, minutes);

        return readableTime;
    }

}
