package com.example.scrollview;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrollview.model.Tasks;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;


public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private List<Tasks> tasks;
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String datetimeString(Tasks tasks) {

        Calendar mycalender = tasks.getmCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        String strDate = dateFormat.format((mycalender.getTime()));


        return strDate + " " + changeFormat(mycalender.get(Calendar.HOUR_OF_DAY),mycalender.get(Calendar.MINUTE));
    }
    private String changeFormat(int hours, int mins) {

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
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }  //converting to 12 hour format
    public TaskRecyclerViewAdapter(Context mContext, List<Tasks> tasks) {
        this.tasks =tasks;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new TaskRecyclerViewAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerViewAdapter.ViewHolder holder, final int position) {

       /* Glide.with(mContext)
                .asBitmap()
                .load(mTaskImages.get(position))
                .into(holder.image);*/


        final Tasks task = tasks.get(position);
        holder.image.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.calender_icon, null));
        holder.title.setText(task.getTitle());
        holder.date.setText(datetimeString(task));
        holder.venu.setText(task.getVenu());
        holder.linearLayout.setBackground(mContext.getDrawable(R.color.grey));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete Reminder")
                        .setMessage("Are you sure you want to delete this Reminder?")


                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent1 = new Intent(mContext,ReminderBroadcast.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                                        HomeFragment.savedTasks.get(position).getID(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                                Log.i("deleted",Integer.toString(HomeFragment.savedTasks.get(position).getID()));
                                AlarmManager am = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                                am.cancel(pendingIntent);
                                HomeFragment.savedTasks.remove(position);
                                SharedPreferences sharedPreferences;
                                if(HomeFragment.savedTasks.size()==0)
                                {
                                    HomeFragment.emptyView.setVisibility(View.VISIBLE);
                                }
                                sharedPreferences = mContext.getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(HomeFragment.savedTasks);
                                prefsEditor.putString("tasks", json);
                                //Log.i(TAG,"task size"+ String.valueOf(HomeFragment.savedTasks.size()));
                                prefsEditor.apply();
                                HomeFragment.taskAdapter.notifyDataSetChanged();
                                //code to delete a alarm

                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .show();
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title;
        TextView date;
        LinearLayout linearLayout;
        TextView venu;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.taskImage);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            venu = itemView.findViewById(R.id.row_venu);
            linearLayout = itemView.findViewById(R.id.taskItemLayout);

        }
    }

}
