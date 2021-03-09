package com.example.todo;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeUtilPro {
    private TimeUtilPro() {
    }

    // ########## LOCALDATE METHODS ##########

    public static LocalDate intToLocalDate(int date) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return LocalDate.parse(String.valueOf(date), dTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException();
        }
    }

    public static LocalDate longToLocalDate(long dateTime) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        try {
            return LocalDate.parse(String.valueOf(dateTime), dTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("deprecation")
    public static LocalDate dateToLocalDate(Date dateTime) {
        try {
            return LocalDate.of(dateTime.getYear() + 1900, dateTime.getMonth() + 1, dateTime.getDate());
        } catch (DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    public static LocalDate calendarToLocalDate(Calendar dateTime) {
        try {
            return LocalDate.of(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH) + 1,
                    dateTime.get(Calendar.DAY_OF_MONTH));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    // ########## LOCALDATETIME METHODS ##########

    public static LocalDateTime intToLocalDateTime(int date) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        try {
            return LocalDateTime.parse(String.valueOf(date) + "0000", dTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException();
        }
    }

    public static LocalDateTime longToLocalDateTime(long dateTime) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        try {
            return LocalDateTime.parse(String.valueOf(dateTime), dTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("deprecation")
    public static LocalDateTime dateToLocalDateTime(Date dateTime) {
        try {
            return LocalDateTime.of(dateTime.getYear() + 1900, dateTime.getMonth() + 1, dateTime.getDate(), 0, 0);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    public static LocalDateTime calendarToLocalDateTime(Calendar dateTime) {
        try {
            return LocalDateTime.of(dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH) + 1,
                    dateTime.get(Calendar.DAY_OF_MONTH), 0, 0);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    // ########## INT METHODS ##########

    public static int localDateToInt(LocalDate date) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return Integer.parseInt(date.format(dTimeFormatter));
        } catch (NumberFormatException | DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    public static int localDateTimeToInt(LocalDateTime dateTime) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return Integer.parseInt(dateTime.format(dTimeFormatter));
        } catch (NumberFormatException | DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    // ########## LONG METHODS ##########

    public static long localDateToLong(LocalDate date) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return Long.parseLong(date.format(dTimeFormatter) + "0000");
        } catch (NumberFormatException | DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    public static long localDateTimeToLong(LocalDateTime dateTime) {
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        try {
            return Long.parseLong(dateTime.format(dTimeFormatter));
        } catch (NumberFormatException | DateTimeException e) {
            throw new IllegalArgumentException();
        }
    }

    // ########## DATE METHODS ##########

    @SuppressWarnings("deprecation")
    public static Date localDateToDate(LocalDate date) {
        return new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth());
    }

    @SuppressWarnings("deprecation")
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return new Date(dateTime.getYear() - 1900, dateTime.getMonthValue() - 1, dateTime.getDayOfMonth());
    }

    // ########## CALENDAR METHODS ##########

    public static Calendar localDateToCalendar(LocalDate date) {
        Calendar c1 = Calendar.getInstance();
        c1.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        return c1;
    }

    public static Calendar localDateTimeToCalendar(LocalDateTime dateTime) {
        Calendar c1 = Calendar.getInstance();
        c1.set(dateTime.getYear(), dateTime.getMonthValue() - 1, dateTime.getDayOfMonth());
        return c1;
    }

}
