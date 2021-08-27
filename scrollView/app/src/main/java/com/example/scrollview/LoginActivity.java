package com.example.scrollview;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.scrollview.model.Attendence;
import com.example.scrollview.model.Schedule;
import com.example.scrollview.model.Subject;
import com.example.scrollview.model.User;
import com.example.scrollview.model.events;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    FirebaseAuth fAuth;
    String phoneNumber = "+917451221244";
    String otpCode = "123456";
    String verificationId;
    EditText phone,optEnter;
    Button next;
    int eflag = 0;


 //   public static events eventList = new events();
    public ArrayList<Attendence> attendences  = new ArrayList<>();

    Gson gson  = new Gson();

    PhoneAuthCredential credential;
    Boolean verificationOnProgress = false;
    AVLoadingIndicatorView progressBar;
    TextView state,resend;
    PhoneAuthProvider.ForceResendingToken token;
    FirebaseFirestore fStore;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_splash_screen);
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        setTheme(R.style.Theme_AppCompat_NoActionBar);

            setContentView(R.layout.activity_login);
            phone = findViewById(R.id.phone);
            optEnter = findViewById(R.id.codeEnter);
            next = findViewById(R.id.nextBtn);
            progressBar = findViewById(R.id.progressBar);
            state = findViewById(R.id.state);
            resend = findViewById(R.id.resendOtpBtn);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
            getDelegate().setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            createNotificationChannel();

            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo:: resend OTP
                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10) {
                        if (!verificationOnProgress) {
                            next.setEnabled(false);
                            progressBar.show();
                            progressBar.setVisibility(View.VISIBLE);
                            state.setVisibility(View.VISIBLE);
                            String phoneNum = "+91" + phone.getText().toString();
                            Log.d("phone", "Phone No.: " + phoneNum);
                            requestPhoneAuth(phoneNum);
                        } else {
                            next.setEnabled(false);
                            optEnter.setVisibility(View.GONE);
                            progressBar.show();
                            progressBar.setVisibility(View.VISIBLE);
                            state.setText("Logging in");
                            state.setVisibility(View.VISIBLE);
                            otpCode = optEnter.getText().toString();
                            if (otpCode.isEmpty()) {
                                optEnter.setError("Required");
                                return;
                            }

                            credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
                            verifyAuth(credential);
                        }

                    } else {
                        phone.setError("Valid Phone Required");
                    }
                }
            });
        }



    private void requestPhoneAuth(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60L, TimeUnit.SECONDS,this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(LoginActivity.this, "OTP Timeout, Please Re-generate the OTP Again.", Toast.LENGTH_SHORT).show();
                        resend.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        token = forceResendingToken;
                        verificationOnProgress = true;
                        progressBar.hide();
                        progressBar.setVisibility(View.GONE);
                        state.setVisibility(View.GONE);
                        next.setText("Verify");
                        next.setEnabled(true);
                        optEnter.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        // called if otp is automatically detected by the app
                        verifyAuth(phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onVerificationFailed: "+e.getMessage());

                    }
                });
    }




    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Phone Verified."+fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,splash_screen.class));
                    finish();
                    //checkUserProfile();
                }else {
                    progressBar.hide();
                    progressBar.setVisibility(View.GONE);
                    state.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Can not Verify phone and Create Account.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
            super.onStart();

       /* if(fAuth.getCurrentUser() != null){

            *//*progressBar.show();
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking..");
            state.setVisibility(View.VISIBLE);*//*

            checkUserProfile();

        }*/
    }

   /* private void checkUserProfile() {
        DocumentReference docRef = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){


                    user = documentSnapshot.toObject(User.class);
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
                Toast.makeText(LoginActivity.this, "Profile Do Not Exists"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
                                for(QueryDocumentSnapshot document : task.getResult()) {

                                    subjects.add(document.toObject(Subject.class));
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
                                SharedPreferences.Editor editor = wmbPreference.edit();
                                editor.putBoolean("FIRSTRUN", false);
                                editor.commit();
                                Log.i(TAG, "onComplete: attendenes"+gson.toJson(attendences));
                            } else {
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
        fStore.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<events.event> events_temp = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                events_temp.add(document.toObject(events.event.class));
                            }

                            events.setCategories(events_temp);
                            eflag = 1;
                            Log.i(TAG, "onComplete: getcategories" +gson.toJson(events.getCategories()));
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });

    }
    public void getTimetable() {
        String[] days ={"sunday","monday","tuesday","wednesday","thursday","friday","saturday",};
        for (final String day : days) {


            fStore.collection(user.getYear()).document(user.getBatch()).collection(day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(LoginActivity.this,splash_screen.class));
        finish();
        return super.onOptionsItemSelected(item);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "hello";
            String description = "byeee";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("hello", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
