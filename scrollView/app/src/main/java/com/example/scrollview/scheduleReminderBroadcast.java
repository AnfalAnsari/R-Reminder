package com.example.scrollview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.scrollview.model.Schedule;
import com.example.scrollview.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static com.example.scrollview.splash_screen.timetable;
import static com.example.scrollview.splash_screen.user;

public class scheduleReminderBroadcast extends BroadcastReceiver {
    private static final String TAG = "scheduleReminderBro";
    private Context Gcontext;
    FirebaseFirestore fStore;
    //public Map<String,ArrayList<Schedule>> timetable = new HashMap<>();
    public String getToday(){

        Date date =  new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.getDefault());
        String dayOfWeek   = sdf.format(date);
        String editedDayOfWeek = dayOfWeek.substring(0, 1).toLowerCase()+dayOfWeek.substring(1);
        return editedDayOfWeek ;
    } // It returns today in the form - "monday"
    public void scheduleToday() {
        // This function will be called either on first run or 1am everyday
        setNotifications(getToday());   // It will set notification for the today
        /*setAlarm(1,min+2,1,"continueLoop","1");*/
        NextDayIntent();  // It will call this function again at 1 am but on next day
    } // This function will be called either on first run or 1am everyday
    public void NextDayIntent() {
       //schedule an intent which will call scheduleToday(written in an if statement of on receive function) at 1 am
       Calendar myCalendar = Calendar.getInstance(Locale.getDefault());
       Log.i(TAG, "setAlarm: hour " + Integer.toString(myCalendar.get(Calendar.HOUR_OF_DAY)));
       String title = "continueLoop";
       String code = "1";
       int mHour = 1;
       int mMinute = 0;
       int ID  =  0;
       Log.i(TAG, "setAlarm: entered");
       Intent intent = new Intent(Gcontext, scheduleReminderBroadcast.class);
       intent.putExtra("name", title);
       intent.putExtra("ID", code);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(Gcontext, ID, intent, 0);
       AlarmManager alarmManager = (AlarmManager) Gcontext.getSystemService(ALARM_SERVICE);
       myCalendar.set(Calendar.HOUR_OF_DAY,mHour);
       myCalendar.set(Calendar.MINUTE, mMinute);
       myCalendar.set(Calendar.SECOND, 10);
       myCalendar.add(Calendar.DATE,1);
       long selectedTimestamp = myCalendar.getTimeInMillis();

       Log.i(TAG, "NextDayIntent: Scheduled"  + title + " " + mHour+":" + mMinute);
       alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimestamp, pendingIntent);
} //This will schedule an intent which will call scheduleToday(written in an if statement of on receive function) at 1 am
    public String splitTime(String time){
        String[] strings = time.split("-");
        return strings[0];
    } // To get starting time of lecture from the string
    public void setNotifications(String d) {

        // This function will schedule all the notification of today

         final String day = d;
         fStore = FirebaseFirestore.getInstance();
        SharedPreferences wmbPreference = Gcontext.getSharedPreferences("com.example.scrollview", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(wmbPreference.getString("user",null),User.class);
        Log.i(TAG, "setNotifications: " + gson.toJson(user));
        fStore.collection(user.getYear()).document(user.getBatch()).collection(day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Schedule> temp= new ArrayList<>();

                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            temp.add(document.toObject(Schedule.class));
                            document.getId();


                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    Calendar sCalendar = Calendar.getInstance();
                    String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                  //  timetable.put(day,temp);
                    Log.i(TAG, "onComplete: timetable "+ day + "   "+ new Gson().toJson(temp));


                    for (int i = 0; i < temp.size() ; i++) {
                            String time=temp.get(i).getTime();
                            String startTime =splitTime(time);
                            String[] strings = startTime.split(":");
                            //setAlarm(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),i+1,timetable.get(day).get(i).getName(),timetable.get(day).get(i).getCode());
                            setNotificationAlarm(temp.get(i),i+1);

                         //   setAlarm(1,34+i,i,"Anfal","1");


                            Log.i(TAG, "onComplete: in for loop"+temp.get(i).getName() + strings[1]);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: "+e);
                }
            });

        } // This function will schedule all the notification of the day passed in the form "monday"
    /*private void setAlarm(int mHour,int mMinute,int ID,String title,String code) {
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
            myCalendar.set(Calendar.SECOND, 10);
            Log.i(TAG, "phase 1" + title);
            Log.i("minute", String.valueOf(myCalendar.SECOND));
            Log.i("minute", String.valueOf(myCalendar.MINUTE));
            long selectedTimestamp = myCalendar.getTimeInMillis();
            Log.i(TAG, "setAlarm: scheduled " + title + " "  + mHour+ ':' + mMinute);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimestamp, pendingIntent);
        }
    }*/
    private void setNotificationAlarm(Schedule temp, int ID) {
        String time=temp.getTime();
        String startTime =splitTime(time);
        String[] strings = startTime.split(":");
        int mHour = Integer.parseInt(strings[0]);
        int mMinute = Integer.parseInt(strings[1]);
        String title = temp.getName();
        String code = temp.getCode();
        String venue = temp.getVenue();
        //setAlarm(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),ID,timetable.get(day).get(i).getName(),timetable.get(day).get(i).getCode());
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
            intent.putExtra("ID",String.valueOf(ID));
            intent.putExtra("venue",venue);
            intent.putExtra("code",code);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Gcontext, ID, intent, 0);
            AlarmManager alarmManager = (AlarmManager) Gcontext.getSystemService(ALARM_SERVICE);
            myCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            myCalendar.set(Calendar.MINUTE, mMinute);
            myCalendar.set(Calendar.SECOND, 10);
            Log.i(TAG, "phase 1" + title);
            Log.i("minute", String.valueOf(myCalendar.SECOND));
            Log.i("minute", String.valueOf(myCalendar.MINUTE));
            long selectedTimestamp = myCalendar.getTimeInMillis();
            Log.i(TAG, "setAlarm: scheduled " + title + " "  + mHour+ ':' + mMinute);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedTimestamp, pendingIntent);
        }
    } // This will schedule notifications for lectures taking schedule type object

    @Override
    public void onReceive(Context context, Intent intent) {
        Gcontext = context;
        String title = intent.getStringExtra("name");
        String id = intent.getStringExtra("ID");
        String code = intent.getStringExtra("code");
        String venue = intent.getStringExtra("venue");
        Log.i("TAG", "Received: " + title + " " + id);

        if(title.equals("continueLoop")){
            // In this case intent is received to schedule notification, not to show notifications
            scheduleToday();
            Log.i(TAG, "onReceive: " + "phase 1 schedule next called");

        }

        else{
            // If the intent is received for showing notification: example this code will run at  saturday 9 am for showing Maths class

            PendingIntent pIntentlogin;
            int NotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            Intent intentAction = new Intent(context, ActionReceiver.class);
            intentAction.putExtra("NotificationId", NotificationId);

            //This is optional if you have more than one buttons and want to differentiate between two
            intentAction.putExtra("action", "actionName");
            pIntentlogin = PendingIntent.getBroadcast(context, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "hello")
                    .setSmallIcon(R.drawable.ic_icons8_checkmark)
                    .setContentTitle(title+id)
                    .setContentText(venue)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true)

                    // Set the intent that will fire when the user taps the notification
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            Intent yesReceive = new Intent(context, ActionReceiver.class);
            yesReceive.putExtra("ID", String.valueOf(NotificationId));
            yesReceive.putExtra("code",code);
            yesReceive.setAction("YES_ACTION");
            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.tick, "Yes", pendingIntentYes);
            Intent yesReceive2 = new Intent(context, ActionReceiver.class);
            yesReceive2.putExtra("ID", String.valueOf(NotificationId));
            yesReceive2.putExtra("code",code);
            yesReceive2.setAction("STOP_ACTION");
            PendingIntent pendingIntentYes2 = PendingIntent.getBroadcast(context, 12345, yesReceive2, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.cross, "No", pendingIntentYes2);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(NotificationId, builder.build());
        }








    }



}
