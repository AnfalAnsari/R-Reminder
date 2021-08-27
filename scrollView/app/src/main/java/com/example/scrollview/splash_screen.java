package com.example.scrollview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scrollview.model.Attendence;
import com.example.scrollview.model.Schedule;
import com.example.scrollview.model.Subject;
import com.example.scrollview.model.User;
import com.example.scrollview.model.events;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class splash_screen extends AppCompatActivity {
    private static final String TAG = "splash_screen";
    public static User user = new User();
    public static Map<String,ArrayList<Schedule>> timetable = new HashMap<>();
    public static ArrayList<Subject> subjects = new ArrayList<>();


    public ArrayList<Attendence> attendences  = new ArrayList<>();

    //Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    int eflag = 0;
    Gson gson = new Gson();
    // UI
    AVLoadingIndicatorView loading;
    Button loginButton;
    TextView registerButton;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loading = findViewById(R.id.Splash_progressBar);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.registerButton);
        textView = findViewById(R.id.textView9);
        if(fAuth.getCurrentUser()!=null)
        {
            registerButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            loading.show();
            checkUserProfile();
        }
        else
        {
            loginButton.setVisibility(View.VISIBLE);
            loading.hide();

        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(splash_screen.this,LoginActivity.class));
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(splash_screen.this,LoginActivity.class));
            }
        });


    }
    private void checkUserProfile(){
        DocumentReference docRef = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new  OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){


                    user = documentSnapshot.toObject(User.class);
                    Log.i(TAG, "onSuccess: " + gson.toJson(user));
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.scrollview", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                    prefsEditor.putString("user", gson.toJson(user));
                    prefsEditor.apply();
                    getTimetable();
                    getsubjectattendance();
                    getEvents();





                }else{
                    //Toast.makeText(Register.this, "Profile Do not Exists.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                    finish();
                }
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(splash_screen.this, "Profile Do Not Exists"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    public void getTimetable() {
        String[] days ={"sunday","monday","tuesday","wednesday","thursday","friday","saturday",};
        for (final String day : days) {


            fStore.collection(user.getYear()).document(user.getBatch()).collection(day).orderBy("time").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Schedule> temp = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            temp.add(document.toObject(Schedule.class));
                            document.getId();

                            //  Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    Calendar sCalendar = Calendar.getInstance();
                    String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

                    //Adding timetable in static array list timetable
                    timetable.put(day, temp);

                    if (dayLongName.equalsIgnoreCase(day)) {
                        for (int i = 0; i < timetable.get(day).size(); i++) {
                            String time = timetable.get(day).get(i).getTime();
                            String startTime = splitTime(time);
                            String[] strings = startTime.split(":");

                            //   setAlarm(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),i,timetable.get(day).get(i).getName(),timetable.get(day).get(i).getCode());
                        }
                    }

                    // run this code if its first run to enter into loop


                    SharedPreferences wmbPreference = getApplicationContext().getSharedPreferences("com.example.scrollview", Context.MODE_PRIVATE);
                    boolean isFirstRun = true; //wmbPreference.getBoolean("FIRSTRUN_NOTIFICATION", true);
                    if (isFirstRun) {

                        Calendar myCalendar = Calendar.getInstance();
                        setAlarm(myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), 0, "continueLoop", "1");
                        Log.i(TAG, "onComplete: phase 1 after set alarm");

                        Log.i("msg", String.valueOf(isFirstRun));
                    } else {
                        Log.i("msg", String.valueOf(isFirstRun));
                    }

                    SharedPreferences.Editor editor = wmbPreference.edit();
                    editor.putBoolean("FIRSTRUN_NOTIFICATION", false);
                    editor.commit();




                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: "+e);
                }
            });

        }

    }
    public  String splitTime(String time){
        String[] strings = time.split("-");
        return strings[0];
    }
    public void getsubjectattendance() {


        fStore.collection(user.getYear()).document(user.getBatch()).collection("subjects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            SharedPreferences wmbPreference = getApplicationContext().getSharedPreferences("com.example.scrollview", Context.MODE_PRIVATE);
                            boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
                            ArrayList<Subject> temp = new ArrayList<>();
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    temp.add(document.toObject(Subject.class));
                                    //subjects.add(document.toObject(Subject.class));
                                    attendences.add(document.toObject(Attendence.class));
                                    Log.i(TAG, "onComplete:  subject" + gson.toJson(subjects));
                                    Log.i(TAG, "onComplete: attendances" + gson.toJson(attendences));



                                    if (isFirstRun)
                                    {
                                        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("com.example.scrollview", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                        Gson gson = new Gson();
                                        String json = gson.toJson(attendences);
                                        prefsEditor.putString("attendence", json);
                                        prefsEditor.apply();

                                        Log.i("msg", String.valueOf(isFirstRun));
                                    }else{
                                        Log.i("msg", String.valueOf(isFirstRun));
                                    }

                                    //  Log.d(TAG, document.getId() + " => " + document.getData());
                                }

                            subjects.addAll(temp);
                            SharedPreferences.Editor editor = wmbPreference.edit();
                            editor.putBoolean("FIRSTRUN", false);
                            editor.commit();
                            Log.i(TAG, "onComplete: attendenes"+gson.toJson(attendences));
                        }

                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e);
            }
        });
    }
    public void getEvents(){
        fStore.collection("events").orderBy("date_time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<events.event> events_temp = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                events_temp.add(document.toObject(events.event.class));
                            }
                            if(user.getAdmin())
                            {
                                events_temp.add(0,new events.event());
                            }

                            events.setCategories(events_temp);

                            eflag = 1;
                            startActivity(new Intent(splash_screen.this,MainActivity.class));
                            finish();


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });

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
            Intent intent = new Intent(getApplicationContext(), scheduleReminderBroadcast.class);
            intent.putExtra("name", title);
            intent.putExtra("ID",code);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ID, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
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