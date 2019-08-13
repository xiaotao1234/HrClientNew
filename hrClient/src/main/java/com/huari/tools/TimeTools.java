package com.huari.tools;

public class TimeTools {
    int minute;
    int second;
    private static TimeTools timeTools;
    private TimeTools(){
    }
    public static TimeTools getInstance(){
        if(timeTools==null){
            timeTools = new TimeTools();
        }
        return timeTools;
    }
    public String timetrans(int secondnum){
        minute = secondnum/60;
        second = secondnum%60;
        if(second<10){
            return minute+":"+"0"+second;
        }else {
            return minute+":"+second;
        }
    }
}
