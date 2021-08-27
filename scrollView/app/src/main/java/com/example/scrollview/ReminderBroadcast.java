package com.example.scrollview;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.scrollview.model.Tasks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ReminderBroadcast extends BroadcastReceiver {
    MediaPlayer mediaPlayer;


    @Override
    public void onReceive(Context context, Intent intent) {
        List<Tasks> arrayItems;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString("tasks", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Tasks>>(){}.getType();
        arrayItems = gson.fromJson(serializedObject, type);
        Integer Id = intent.getIntExtra("name", 0);
        Integer not = intent.getIntExtra("notif", 0);
        Integer ala = intent.getIntExtra("alarm", 0);



        int position=-1;
        Log.i("TAG", "onReceive: entered");
        for (int i = 0; i <arrayItems.size() ; i++) {
            Log.i("for i", Integer.toString(i));
            Log.i("onreceive",Integer.toString(arrayItems.get(i).getID()) );
            if(arrayItems.get(i).getID()==Id){
                position=i;
                Log.i("position", Integer.toString(position));
                break;
            }
        }

           if(position!=-1){

if(not==1) {
    Log.i("msg", "notification acriv");
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "hello")
            .setSmallIcon(R.drawable.ic_icons8_checkmark)
            .setContentTitle("Scrollview")
            .setContentText(arrayItems.get(position).getTitle()+ "   "+"Venue:"+" "+ arrayItems.get(position).getVenu())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true);

    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    builder.setSound(alarmSound);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
    notificationManager.notify(Id, builder.build());
    Log.i("gvhgvgh", String.valueOf(position));
    SharedPreferences sharedPreferences1;
    HomeFragment.savedTasks.remove(position);
    if (HomeFragment.savedTasks.size() == 0) {
        HomeFragment.emptyView.setVisibility(View.VISIBLE);
    }

    sharedPreferences1 = context.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences1.edit();
    Gson gson1 = new Gson();
    String json = gson1.toJson(HomeFragment.savedTasks);
    prefsEditor.putString("tasks", json);
    //Log.i(TAG,"task size"+ String.valueOf(HomeFragment.savedTasks.size()));
    prefsEditor.apply();
    HomeFragment.taskAdapter.notifyDataSetChanged();

}
else if(ala==1){

    mediaPlayer = MediaPlayer.create(context, R.raw.notific);
    mediaPlayer.start();

Intent intent1 = new Intent(context, Alarm.class);


//This Intent will be called when Notification will be clicked by user.
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request
  code */, intent1,
            PendingIntent.FLAG_CANCEL_CURRENT);

    Log.i("msg", "notification acriv");
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "hello")
            .setSmallIcon(R.drawable.ic_icons8_checkmark)
            .setContentTitle("Scrollview")
            .setContentText(arrayItems.get(position).getTitle()+"/r/n  "+"Venue:"+" "+ arrayItems.get(position).getVenu())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.mipmap.ic_launcher,"STOP",pendingIntent)
            // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true);

    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    builder.setSound(alarmSound);
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    SharedPreferences sharedPreferences1;
// notificationId is a unique int for each notification that you must define
    notificationManager.notify(Id, builder.build());
    Log.i("gvhgvgh", String.valueOf(position));
    HomeFragment.savedTasks.remove(position);
    if (HomeFragment.savedTasks.size() == 0) {
        HomeFragment.emptyView.setVisibility(View.VISIBLE);
    }

    sharedPreferences1 = context.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = sharedPreferences1.edit();
    Gson gson1 = new Gson();
    String json = gson1.toJson(HomeFragment.savedTasks);
    prefsEditor.putString("tasks", json);
    //Log.i(TAG,"task size"+ String.valueOf(HomeFragment.savedTasks.size()));
    prefsEditor.apply();
    HomeFragment.taskAdapter.notifyDataSetChanged();

}
           }




        }



}
