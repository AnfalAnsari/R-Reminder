package com.example.scrollview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.SweepGradient;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrollview.model.Attendence;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import me.itangqi.waveloadingview.WaveLoadingView;

class subjectAdapter1 extends RecyclerView.Adapter<subjectAdapter1.ViewHolder> implements ExampleDialog.ExampleDialogListener {
    private Context context;
    private List<Attendence> subjects;
    SharedPreferences mPrefs;



    public subjectAdapter1 (Context context, List<Attendence> subjects) {
        this.context = context;
        this.subjects =  subjects;

    }

    @NonNull
    @Override
    public subjectAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_item1,parent,false);
       // SharedPreferences wmbPreference = context. getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);

            subjects =  getList();


        return new subjectAdapter1.ViewHolder(view);


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final subjectAdapter1.ViewHolder holder, final int position) {


        final Attendence subject = subjects.get(position);
        holder.name.setText(subject.getName());
        holder.code.setText(subject.getCode());
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu= new PopupMenu(context, holder.menuButton);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_drawer_item1:
                                //handle menu1 click
                                if (subject.getPrevRecord().size()>0)
                                {
                                    if (subject.getPrevRecord().get(subject.getPrevRecord().size()-1)==true) {

                                        subject.setPresent(subject.getPresent() - 1);
                                        subject.setTotal(subject.getTotal()-1);
                                        holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
                                        saveSubject(position,subject);


                                    }
                                    else {

                                        subject.setTotal(subject.getTotal()-1);
                                        holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
                                        saveSubject(position,subject);
                                    }
                                    subject.undoPrevRecord();
                                    saveSubject(position,subject);
                                    Toast.makeText(context, "undo", Toast.LENGTH_SHORT).show();


                                }
                                else {
                                    Toast.makeText(context, "cannot undo", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            case R.id.navigation_drawer_item2:
                                //handle menu2 click
                                subject.setPresent(0);
                                subject.setStatus("");
                                subject.setPercentage(0);
                                subject.setTotal(0);
                                subject.resetPrevRecord();
                                holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
                                Toast.makeText(context, "reset", Toast.LENGTH_SHORT).show();
                                saveSubject(position,subject);
                                return true;
                            case R.id.navigation_drawer_item3:
                                //handle menu3 click
                                openDialog();
                                Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
                                saveSubject(position,subject);
                                return true;
                            default:
                                return false;
                        }


                    }
                });
                popupMenu.show();

            }
        });

      //holder.progressBar.setMax(100);
//      holder.percentage.setText(subject.getPercentage()+"%");
         holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
      //holder.progressBar.setProgress((int) subject.getPercentage(),true);
        holder.mWaveLoadingView.setTextSize(50);
        holder.mWaveLoadingView.setMaxValue(100);
        holder.mWaveLoadingView.setBarWidth(20);
        holder.mWaveLoadingView.setRimWidth(25);
        holder.mWaveLoadingView.setValue(0);
        holder.mWaveLoadingView.setValue((float) subject.getPercentage());
      holder.status.setText(subject.getstatus());


        holder.right.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                subject.setPresent(subject.getPresent()+1);
                subject.setTotal(subject.getTotal()+1);
                subject.setPrevRecord(subject.getPrevRecord(),true);


             //   Log.i("msg", String.valueOf(a[position]));
           //     Log.i("position", String.valueOf(position));

            //   Log.i("msg", String.valueOf(c));
               // holder.progressBar.setProgress((int) subject.getPercentage(),true);
                holder.mWaveLoadingView.setValue((float) subject.getPercentage());
               // holder.percentage.setText(subject.getPercentage()+"%");
                holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());

                if(subject.getPercentage()>75){
                    double percentage=  subject.getPercentage();
                    double attended=  subject.getPresent();
                    double total=  subject.getTotal();
                    int count = 0;
                    while (percentage>75){
                        percentage=attended/(total+1)*100;
                        total=total+1;
                        if(percentage>=75) count++;
                        Log.i("mission percent", "onClick: "+Double.toString(percentage));
                        Log.i("mission attended", "onClick: "+Double.toString(attended));
                        Log.i("mission total", "onClick: "+Double.toString(total));
                        Log.i("mission count", "onClick: "+Integer.toString(count));
                    }
                    if (count==0){
                        holder.status.setText("Status: On track,you should attend your next class");
                    }
                    else if(count==1){
                    holder.status.setText("Status: On track, you can leave your next class");
                    subject.setStatus("Status: On track, you can leave your next class");}
                    else if(count>1) {
                        holder.status.setText("Status: On track, you can leave your next "+Integer.toString(count)+" classes");
                    }
                    holder.mWaveLoadingView.setBarColor(Color.GREEN);
                }
                else if(subject.getPercentage()==75){
                    holder.status.setText("Status: On track,you should attend your next class");
                    subject.setStatus("Status: On track,you should attend your next class");}
                else{
                    double percentage=  subject.getPercentage();
                    double attended=  subject.getPresent();
                    double total=  subject.getTotal();
                    int count = 0;
                    while (percentage<75){
                        percentage=(attended+1)/(total+1)*100;
                        attended+=1;
                        total+=1;
                        count++;
                    }
                    if (count==1)   {holder.status.setText("Status: You should attend your next class to get back on track");
                    subject.setStatus("Status: You should attend your next class to get back on track");}
                    else{
                    holder.status.setText("Status: You should attend your next " +Integer.toString(count)+" classes to get back on track");
                    subject.setStatus("Status: You should attend your next " +Integer.toString(count)+" classes to get back on track");
                    }
                    holder.mWaveLoadingView.setBarColor(Color.RED);
                    }

                mPrefs =context. getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                subjects.set(position,subject) ;
                Gson gson = new Gson();
                String json = gson.toJson(subjects);
                prefsEditor.putString("attendence", json);
                prefsEditor.apply();

            }
        });

        holder.cross.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                subject.setTotal(subject.getTotal()+1);

                holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
            //    Log.i("msg", String.valueOf(c));
               // holder.progressBar.setProgress((int) subject.getPercentage(),true);
                holder.mWaveLoadingView.setValue((float) subject.getPercentage());
                subject.setPrevRecord(subject.getPrevRecord(),false);
               // holder.percentage.setText(subject.getPercentage()+"%");
                subjects.set(position,subject);


                 if(subject.getPercentage()>75){
                     double percentage=  subject.getPercentage();
                     double attended=  subject.getPresent();
                     double total=  subject.getTotal();
                     int count = 0;
                     while (percentage>75){
                         percentage=attended/(total+1)*100;
                         total=total+1;
                         if(percentage>=75) count++;
                         Log.i("mission percent", "onClick: "+Double.toString(percentage));
                         Log.i("mission attended", "onClick: "+Double.toString(attended));
                         Log.i("mission total", "onClick: "+Double.toString(total));
                     }
                     if(count==0){
                         holder.status.setText("Status: On track, you can't miss your next class");
                         subject.setStatus("Status: On track, you can't miss your next class");
                     }

                     else if(count==1){
                         holder.status.setText("Status: On track, you can leave your next class");
                         subject.setStatus("Status: On track, you can leave your next class");}
                     else if(count>1) {
                         holder.status.setText("Status: On track, you can leave your next "+Integer.toString(count)+" classes");
                     }
                    holder.mWaveLoadingView.setBarColor(Color.GREEN);

                }
                else if(subject.getPercentage()==75){
                    holder.status.setText("Status: You should attend your next class");
                    subject.setStatus("Status: You should attend your next class");}
                else{
                     double percentage=  subject.getPercentage();
                     double attended=  subject.getPresent();
                     double total=  subject.getTotal();
                     int count = 0;
                     while(percentage<75){
                         percentage=(attended+1)/(total+1)*100;
                         attended+=1;
                         total+=1;
                         count++;
                         Log.i("count", "onClick: "+ Double.toString(count));
                     }
                     if (count==1){holder.status.setText("Status: You should attend your next class to get back on track");
                         subject.setStatus("Status: You should attend your next class to get back on track");}
                     else{
                         holder.status.setText("Status: You should attend your next " +Integer.toString(count)+" classes to get back on track");
                         subject.setStatus("Status: You should attend your next " +Integer.toString(count)+" classes to get back on track");
                     }
                    holder.mWaveLoadingView.setBarColor(Color.RED);
                    }

                mPrefs =context. getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                Gson gson = new Gson();
                String json = gson.toJson(subjects);
                prefsEditor.putString("attendence", json);
                prefsEditor.apply();


            }
        });


        final CardView view = holder.cardView;
        /*@Override
        public void applyTexts(String username, String password) {



        }*/


    }

    void saveSubject(int position, Attendence subject){
        mPrefs =context. getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        subjects.set(position,subject) ;
        Gson gson = new Gson();
        String json = gson.toJson(subjects);
        prefsEditor.putString("attendence", json);
        prefsEditor.apply();}



    @Override
    public int getItemCount() {
        return subjects.size();
    }
    public void openDialog() {
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username, String password) {
      //  holder.percent.setText(subject.getPresent()+"/"+subject.getTotal());
    }


    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;
        private TextView code;
        private CardView cardView;
        private ProgressBar progressBar;
        public TextView attendance;
        private ImageView right;
        private ImageView cross;
       // private TextView percentage;
        private TextView percent;
        private TextView status;
        private CircleProgressView mWaveLoadingView;
        private Button menuButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.sub_name);
            name = itemView.findViewById(R.id.desc);
            cardView = itemView.findViewById(R.id.cardView);
            attendance = itemView.findViewById(R.id.textView5);
            right = itemView.findViewById(R.id.imageView);
            cross = itemView.findViewById(R.id.imageView3);
            percent = itemView.findViewById(R.id.textView7);
            status = itemView.findViewById(R.id.textView1);
            mWaveLoadingView = (CircleProgressView) itemView.findViewById(R.id.circleProgressView);
            menuButton = (Button) itemView.findViewById(R.id.menu_button);
        }
    }


    public List<Attendence> getList() {
        List<Attendence> arrayItems;

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString("attendence", null);
        if (serializedObject != null) {


            Gson gson = new Gson();
            Type type = new TypeToken<List<Attendence>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);

            return arrayItems;
        }
        else
        {
            Log.i("msg","123");
            return new ArrayList<>();
        }
    }


}
