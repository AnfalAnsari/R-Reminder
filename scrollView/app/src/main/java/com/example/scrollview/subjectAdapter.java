package com.example.scrollview;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrollview.model.Subject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static com.example.scrollview.splash_screen.user;

class subjectAdapter extends RecyclerView.Adapter<subjectAdapter.ViewHolder> {
    private Context context;
    private List<Subject> subjects;

    public subjectAdapter(Context context, List<Subject> subjects) {
     this.context = context;
     this.subjects =  subjects;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_item,parent,false);


        return new subjectAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull subjectAdapter.ViewHolder holder, final int position) {
        final Subject subject = subjects.get(position);
        holder.name.setText(subject.getName());
        holder.code.setText(subject.getCode());
        holder.profName.setText(subject.getProfName());


        final ConstraintLayout layout = holder.expandableView;
        final Button button = holder.arrowBtn;
        final CardView view = holder.cardView;

        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + subjects.get(position).getEmailID()));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                    intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                    context.startActivity(intent);
                }catch(ActivityNotFoundException e){
                    //TODO smth
                }
            }
        });
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(subjects.get(position).getNumber()));
                context.startActivity(intent);
            }
        });
        holder.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(subjects.get(position).getProfileURL()));
                context.startActivity(intent);
            }
        });

        holder.arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(view, new AutoTransition());
                    layout.setVisibility(View.VISIBLE);
                    button.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    TransitionManager.beginDelayedTransition(view, new AutoTransition());
                    layout.setVisibility(View.GONE);
                    button.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);

                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(user.getAdmin()) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Subject")
                            .setMessage("Are you sure you want to delete this Subject?")


                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                    fStore.collection(user.getYear()).document(user.getBatch()).collection("subjects")
                                            .document(subject.getCode()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "Subject deleted successfully", Toast.LENGTH_SHORT).show();

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
        return subjects.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder
    {

        public ConstraintLayout expandableView;
        public TextView name;
        public TextView code;
        public Button arrowBtn;
        public TextView profName;
        public CardView cardView;
        public Button email,phone,website;
        public Object progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sub_name);
            code = itemView.findViewById(R.id.desc);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
            expandableView = itemView.findViewById(R.id.expandableView);
            cardView = itemView.findViewById(R.id.cardView);
            email=itemView.findViewById(R.id.prof_mail);
            phone=itemView.findViewById(R.id.prof_phone);
            website=itemView.findViewById(R.id.prof_site);
            profName = itemView.findViewById(R.id.subject_profName);



        }
    }

}
