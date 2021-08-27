package com.example.scrollview;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.scrollview.model.Attendence;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ActionReceiver extends BroadcastReceiver {

       SharedPreferences mPrefs;
    @Override
    public void onReceive(Context context, Intent intent) {
       String id = intent.getStringExtra("ID");
       String code = intent.getStringExtra("code");
       int NotificationId= intent.getIntExtra("NotificataionId",0);
        Log.i("Action Receiver", "onReceive: "+Integer.toString(NotificationId));
      // Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        List<Attendence> arrayItems = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString("attendence", null);
        if (serializedObject != null) {

            Gson gson = new Gson();
            Type type = new TypeToken<List<Attendence>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        else
        {
            Log.i("msg","123");
        }

        for(int i=0;i<arrayItems.size();i++){
            cancelNotification(context,Integer.valueOf(id));

            Log.i("bwr",arrayItems.get(i).getCode());
            if(code.equals(arrayItems.get(i).getCode())){
                Log.i("matched","hurahh");
                String action = intent.getAction();
                if ("YES_ACTION".equals(action)) {
                    cancelNotification(context,Integer.valueOf(id));
                    Toast.makeText(context, "YES CALLED", Toast.LENGTH_SHORT).show();

                    arrayItems.get(i).setPresent(arrayItems.get(i).getPresent()+1);
                    arrayItems.get(i).setTotal(arrayItems.get(i).getTotal()+1);
                    if(arrayItems.get(i).getPercentage()>90){

                        arrayItems.get(i).setStatus("Chill Out , You can bunk classes !, Go on a trip");}
                    else if(arrayItems.get(i).getPercentage()>75){

                        arrayItems.get(i).setStatus("You can bunk classes , attendence is maintained");}
                    else if(arrayItems.get(i).getPercentage()==75){

                        arrayItems.get(i).setStatus("You should attend your next class");}
                    else{

                        arrayItems.get(i).setStatus("ALERT:Attend you next few classes ! "    );}
                }


                else  if ("STOP_ACTION".equals(action)) {
                    cancelNotification(context,Integer.valueOf(id));
                    Toast.makeText(context, "STOP CALLED", Toast.LENGTH_SHORT).show();

                    arrayItems.get(i).setPresent(arrayItems.get(i).getPresent());
                    arrayItems.get(i).setTotal(arrayItems.get(i).getTotal()+1);
                    if(arrayItems.get(i).getPercentage()>90){

                        arrayItems.get(i).setStatus("Chill Out , You can bunk classes !, Go on a trip");}
                    else if(arrayItems.get(i).getPercentage()>75){

                        arrayItems.get(i).setStatus("You can bunk classes , attendence is maintained");}
                    else if(arrayItems.get(i).getPercentage()==75){

                        arrayItems.get(i).setStatus("You should attend your next class");}
                    else{

                        arrayItems.get(i).setStatus("ALERT:Attend you next few classes ! "    );}

                }

                mPrefs =context. getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                Gson gson = new Gson();
                String json = gson.toJson(arrayItems);
                prefsEditor.putString("attendence", json);
                prefsEditor.apply();

            }
        }



        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        Log.i("CancelNotification", "cancelNotification: called "+ notifyId);
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
       // nMgr.cancelAll();
        nMgr.cancel(notifyId);
    }



}