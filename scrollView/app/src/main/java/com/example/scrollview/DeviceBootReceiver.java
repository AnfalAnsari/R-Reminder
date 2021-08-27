package com.example.scrollview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class DeviceBootReceiver extends BroadcastReceiver {
    private static final String TAG = "DeviceBootReceiver";
    Context Gcontext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context, "Device rebooted", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "onReceive: " + "Device Rebooted");
            Gcontext = context;
            Calendar myCalendar = Calendar.getInstance();
            setAlarm(myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), 0, "continueLoop", "1");


        }
    }
    private void setAlarm(int mHour, int mMinute, int ID, String title, String code) {
        Calendar myCalendar = Calendar.getInstance(Locale.getDefault());
        Log.i(TAG, "setAlarm: hour "+Integer.toString(myCalendar.get(Calendar.HOUR_OF_DAY)));
        boolean check=false;
        if (mHour>myCalendar.get(Calendar.HOUR_OF_DAY))
            check=true;
        Log.i(TAG, "setAlarm: hour"+Boolean.toString(check)+"mHour: "+Integer.toString(mHour)+" myCalendar: "+Integer.toString(myCalendar.get(Calendar.HOUR_OF_DAY)));

        if (myCalendar.get(Calendar.HOUR_OF_DAY)==mHour&&myCalendar.get(Calendar.MINUTE)<=mMinute)
            check=true;
        Log.i(TAG, "setAlarm: minute "+Boolean.toString(check)+"mMinute: "+Integer.toString(mMinute)+" myCalendar: "+Integer.toString(myCalendar.get(Calendar.MINUTE)));

        Log.i(TAG, "setAlarm: "+Boolean.toString(check));
        if (check) {
            Log.i(TAG, "setAlarm: entered");
            Intent intent = new Intent(Gcontext, scheduleReminderBroadcast.class);
            intent.putExtra("name", title);
            intent.putExtra("ID",code);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Gcontext, ID, intent, 0);
            AlarmManager alarmManager = (AlarmManager) Gcontext.getSystemService(ALARM_SERVICE);
            myCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            myCalendar.set(Calendar.MINUTE, mMinute);
            myCalendar.set(Calendar.SECOND, 0);

            Log.i("msg", String.valueOf(Integer.toString(ID)));
            Log.i("minute", String.valueOf(myCalendar.SECOND));
            Log.i("minute", String.valueOf(myCalendar.MINUTE));


            long selectedTimestamp = myCalendar.getTimeInMillis();

            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimestamp, pendingIntent);
        }
    }
}