package com.mioshek.theclock.assets;


//public class Alarm {
//    private Time time;
//    private Time ringTime;
//    private AlarmState state;
//
//    enum AlarmState {
//        OFF, SET, RINGING
//    }
//
//    public Alarm(Time time) {
//        this.time = time;
//        this.ringTime = time.copy();
//        this.state = AlarmState.OFF;
//    }
//
//    public void setAlarm() {
//        ringTime = time.copy();
//        state = AlarmState.SET;
//        setAlarmInSystem();
//    }
//
//    public void setTime(Time time) {
//        this.time = time;
//    }
//    public Time getRingTime() {
//        return ringTime;
//    }
//
//    public void postponeBy(int minutes) {
//        // must not be postponed by more than 60 minutes or 0 minutes
//        if (minutes < 1 || minutes > 60) {
//            throw new IllegalArgumentException("Illegal postpone time = " + minutes + ", handle it here or prevent from UI");
//        }
//        Time postponedTime = ringTime.copy();
//        int newMinute = postponedTime.minute + minutes;
//        if (newMinute >= 60) {
//            newMinute = newMinute - 60;
//            if (++postponedTime.hour >= 24) {
//                postponedTime.hour = 0;
//            }
//        }
//        postponedTime.minute = newMinute;
//        ringTime = postponedTime;
//        state = AlarmState.SET;
//        setAlarmInSystem();
//    }
//
//    public void setAlarmInSystem() {
//        /*Intent intent = new Intent(this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);*/
//    }
//
//    public void turnOff() {
//        state = AlarmState.OFF;
//    }
//}
