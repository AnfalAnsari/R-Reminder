package com.example.scrollview;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scrollview.model.Tasks;
import com.example.scrollview.model.events;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class reminderActivity extends AppCompatActivity {

    private static final String TAG = "reminderActivity";


    //Flag:Notification and Alarm button
    private boolean nFlag = false;
    private boolean aFlag = false;
    final Calendar myCalendar = Calendar.getInstance();
    private int mYear, mMonth, mHour, mMinute, mDay;


    Tasks task = new Tasks();
    SharedPreferences mPrefs;
    int not = 0;
    int ala = 0;

    //notification and alarm onclick listener
    public void notification(View view) {

        if (nFlag) {
            nFlag = false;
            view.setAlpha(0.5f);
            not = 0;
        } else {
            nFlag = true;
            view.setAlpha(1);
            not = 1;
        }

    }

    public void alarm(View view) {

        if (aFlag) {
            aFlag = false;
            view.setAlpha(0.5f);
            ala = 0;

        } else {
            aFlag = true;
            view.setAlpha(1);
            ala = 1;
        }

    }

    static int a;

    //mDate and mTime:EditText
    TextInputEditText mDate;
    TextInputEditText mTime;
    TextInputEditText venu;
    TextInputEditText name;
    Button notif;
    Button alarm;
    //spinner (reminder type):
    Spinner spin;
    String[] type = {"Academics", "Groups", "Personal"};


    //Onclick for tick:
    public void submit(View view) {
        String text = spin.getSelectedItem().toString();
        if (name.getText().toString().trim().equals("")) {
            name.setError("Name is required!");
        }
        else if (mDate.getText().toString().equals("")){
            mDate.setError("Date is required!");
        }
        else if (mTime.getText().toString().equals("")) {
            mTime.setError("Time is required!");
        }
        else if (venu.getText().toString().equals("")) {
            venu.setError("Venue is required!");
        }
        else if (not == 0 && ala == 0) {
            Toast.makeText(this, "Choose between alarm and notification", Toast.LENGTH_SHORT).show();
        } else {

            task.setmCalendar(myCalendar);
            task.setType(text);
            task.setTitle(name.getText().toString());
            task.setVenu(venu.getText().toString());
            String uniqueId = String.valueOf(myCalendar.get(Calendar.DAY_OF_MONTH)) + String.valueOf(myCalendar.get(Calendar.MONTH)) + String.valueOf(myCalendar.get(Calendar.HOUR_OF_DAY)) + String.valueOf(myCalendar.get(Calendar.MINUTE));
            task.setID(Integer.parseInt(uniqueId));


            mPrefs = getSharedPreferences("com.example.scrollview", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            if (HomeFragment.emptyView.getVisibility() == View.VISIBLE) {
                HomeFragment.emptyView.setVisibility(View.GONE);
            }
            HomeFragment.savedTasks.add(task);
            Gson gson = new Gson();
            String json = gson.toJson(HomeFragment.savedTasks);
            prefsEditor.putString("tasks", json);
            Log.i(TAG, "task size" + String.valueOf(HomeFragment.savedTasks.size()));
            prefsEditor.apply();
            showNotif();
            HomeFragment.taskAdapter.notifyDataSetChanged();
            finish();
        }
    }


    private void showNotif() {
        Intent intent = new Intent(getApplicationContext(),ReminderBroadcast.class);
        intent.putExtra("name",task.getID());
        intent.putExtra("notif",not);
        intent.putExtra("alarm",ala);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), task.getID() ,intent,0);
        AlarmManager alarmManager =(AlarmManager) getSystemService(ALARM_SERVICE);
         long mTime = System.currentTimeMillis();
         long timesec = 1000*10;

        myCalendar.set(Calendar.MONTH, --mMonth);
        myCalendar.set(Calendar.YEAR, mYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        myCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        myCalendar.set(Calendar.MINUTE, mMinute);
        myCalendar.set(Calendar.SECOND, 0);

        Log.i("msg", String.valueOf(task.getID()));
        Log.i("minute", String.valueOf(mMinute));
        Log.i("minute", String.valueOf(mHour));


        long selectedTimestamp =  myCalendar.getTimeInMillis();


        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,selectedTimestamp,pendingIntent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        venu = findViewById(R.id.venu);
        mDate = (TextInputEditText) findViewById(R.id.event_date);
        mTime = (TextInputEditText)findViewById(R.id.event_time);
        spin = (Spinner) findViewById(R.id.spinner);
        name = findViewById(R.id.event_name);
        notif = findViewById(R.id.ntfcbtn);
        alarm = findViewById(R.id.alrmbtn);


        // Case when user enters here from events activity by pressing Remind Me button
          Intent intent = getIntent();
          String jsonString = intent.getStringExtra("events_info");
          Log.i(TAG, "onCreate: " + jsonString);
          if(jsonString!= null)
        {   Gson gson = new Gson();
            events.event event = gson.fromJson(jsonString,events.event.class);
            setFieldThroughEventObject(event);
        }


        //For type of reminder
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_row,type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        //Taking mDate in Edit Text using DatePicker and on focus change listener
        final DatePickerDialog.OnDateSetListener datelistener = new DatePickerDialog.OnDateSetListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                monthOfYear ++;
                mDay = dayOfMonth;
                mMonth = monthOfYear;
                mYear = year;
                updateLabel();
                mDate.clearFocus();
            }

        };

        mDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DatePickerDialog datePickerDialog =new DatePickerDialog(reminderActivity.this, datelistener, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDate.clearFocus();
                    }
                });
                    mDate.setError(null);

                }
            }

        });

        //Taking mTime in EditText using onFocusChange listener and TimePicker Dialog box


        mTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar mcurrentTime = Calendar.getInstance();
             //   int mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
             //       int mMinute = mcurrentTime.get(Calendar.MINUTE);
                    final int AMPM = mcurrentTime.get(Calendar.AM_PM);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(reminderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            updateTime(selectedHour,selectedMinute);
                            myCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                            myCalendar.set(Calendar.MINUTE,selectedMinute);
                            mHour = selectedHour;
                            mMinute =selectedMinute;

                        }
                    }, mHour, mMinute, false);
                    mTimePicker.setTitle("Select mTime");
                    mTimePicker.show();
                    mTime.setError(null);
                }
                mTime.clearFocus();
            }

        });
        venu.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    venu.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    return true;
                }
                return false;
            }
        });



    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        mDate.setText(sdf.format(myCalendar.getTime()));
    }                    //feeding mDate to Edit Text
    private void updateTime(int hours, int mins) {


        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);


        // Append in a StringBuilder
        String aTime = String.valueOf(hours) + ':' +
                minutes + " " + timeSet;
        myCalendar.set(Calendar.HOUR_OF_DAY,hours);
        myCalendar.set(Calendar.MINUTE,mins);
        mTime.setText(aTime);
    }  //converting to 12 mHour format and feeding in Edit text
    private List<Tasks> getList() {
        List<Tasks> arrayItems;
      //  SharedPreferences sharedPreferences =getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        String serializedObject = mPrefs.getString("tasks", null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tasks>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
            Log.i("TAG", serializedObject);
            Log.i("size", String.valueOf(arrayItems.size()));
            return arrayItems;
        }
        else
        {
            List<Tasks> dTasks = new ArrayList<>();
           // dTasks.add(new Tasks());
            Log.i(TAG, "serializedObjectNull");
            return dTasks;


        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setFieldThroughEventObject(events.event eventObject)
    {
        name.setText(eventObject.getTitle());
        venu.setText(eventObject.getVenu());

        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Calendar eventCalendar =  Calendar.getInstance();
        eventCalendar.setTime(eventObject.getDate_time().toDate());
        mDate.setText(sdf.format(eventCalendar.getTime()));
        updateTime(eventCalendar.get(Calendar.HOUR_OF_DAY),eventCalendar.get(Calendar.MINUTE));

    }



}