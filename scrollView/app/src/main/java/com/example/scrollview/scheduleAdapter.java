package com.example.scrollview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrollview.model.Schedule;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.scrollview.splash_screen.user;

public class scheduleAdapter extends RecyclerView.Adapter<scheduleAdapter.ViewHolder> {

    private static final String TAG = "scheduleAdapter";
    private List<Schedule> list;
    private Context mContext;

    public scheduleAdapter(Context context, List<Schedule> schedules) {
       this.list= (ArrayList<Schedule>) schedules;

       this.mContext=context;

    }

    @NonNull
    @Override
    public scheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_row, parent, false);
        return new scheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull scheduleAdapter.ViewHolder holder, final int position) {
        final Schedule schedule = list.get(position);
        holder.subjectCode.setText(schedule.getCode());
        holder.subjectName.setText(schedule.getName());
        holder.profName.setText(schedule.getProfName());
        holder.venue.setText(schedule.getVenue());
        holder.time.setText(schedule.getTime());
        holder.type.setText(schedule.getType());

            if (schedule.getType().equals("Tutorial")){
                holder.design.setBackground(mContext.getDrawable(R.drawable.bg_lecture));}
            else if (schedule.getType().equals("Lecture")){
                holder.design.setBackground(mContext.getDrawable(R.drawable.bg_tutorial));}
            else if(schedule.getType().equals("Practical"))
            {holder.design.setBackground(mContext.getDrawable(R.drawable.bg_practical));}



        final Button button= holder.arrowBtn;
        final ConstraintLayout constraintLayout= holder.expandableLayout;
        final CardView cardView = holder.cardView;
        final TextView subjectCode = holder.subjectCode;
        final TextView profName = holder.profName;
        final TextView type = holder.type;

        holder.arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constraintLayout.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    Log.d(TAG, "onClick: before "+constraintLayout.getVisibility());
                    subjectCode.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    type.setVisibility(View.VISIBLE);
                    profName.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: after"+ constraintLayout.getVisibility());
                    button.animate().setDuration(500).rotationXBy(180);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    subjectCode.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.GONE);
                    type.setVisibility(View.GONE);
                    profName.setVisibility(View.GONE);
                    button.animate().setDuration(500).rotationXBy(-180);

                }
            }
        });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (user.getAdmin())
                    {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Delete Schedule")
                                .setMessage("Are you sure you want to delete this Schedule?")


                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                        fStore.collection(user.getYear()).document(user.getBatch()).collection(scheduleFragment.day)
                                                .document(schedule.getTime()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(mContext, "Schedule deleted successfully" + scheduleFragment.day, Toast.LENGTH_SHORT).show();
                                                        /*splash_screen.timetable.get(day).remove(position);
                                                        scheduleFragment.schedules.remove(position);
                                                        notifyDataSetChanged();*/
                                                    }
                                                });
                                        // Continue with delete operation
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .show();
                }
                    return false;
                }
            });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView subjectName;
        TextView venue;
        TextView time;
        ConstraintLayout expandableLayout;
        CardView cardView;
        Button arrowBtn;
        TextView subjectCode;
        TextView profName;
        TextView type;
        TextView design;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectName =itemView.findViewById(R.id.schedule_subject);
            venue = itemView.findViewById(R.id.schedule_venue);
            time = itemView.findViewById(R.id.schedule_time);
            expandableLayout = itemView.findViewById(R.id.expandable_schedule);
            cardView = itemView.findViewById(R.id.schedule_card);
            arrowBtn = itemView.findViewById(R.id.arrow_btn);
            subjectCode = itemView.findViewById(R.id.subjectCode);
            profName = itemView.findViewById(R.id.prof_name);
            type = itemView.findViewById(R.id.type);
            design = itemView.findViewById(R.id.design);
        }
    }
}
