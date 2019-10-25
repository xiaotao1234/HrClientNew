package com.huari.dataentry;

public class DateString {

    String date;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    int year;
    int month;
    int day;
    int num = 0;

    public void addNum() {
        num++;
    }

    public int getNum() {
        return num;
    }

    public void reduceNum() {
        num--;
    }

    public DateString(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        num++;
        String months = String.format("%02d", month);
        String days = String.format("%02d", day);
        date = year + "-" + months + "-" + days;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateString) {
            if (date.equals(((DateString) obj).getDate())) {
                return true;
            }
        }
        return false;
    }
}
