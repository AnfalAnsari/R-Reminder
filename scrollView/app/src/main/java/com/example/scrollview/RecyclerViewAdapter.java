package com.example.scrollview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.scrollview.model.events;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import static com.example.scrollview.HomeFragment.layoutManager;
import static com.example.scrollview.HomeFragment.recyclerView;
import static com.example.scrollview.MainActivity.bottomNavigationView;
import static com.example.scrollview.MainActivity.dummyView;
import static com.example.scrollview.MainActivity.eventsLayout;
import static com.example.scrollview.R.drawable.blue;
import static com.example.scrollview.R.drawable.dark_blue;
import static com.example.scrollview.R.drawable.orange;
import static com.example.scrollview.R.drawable.red;
import static com.example.scrollview.splash_screen.user;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private List<events.event> events;
    private Context mContext;
    private boolean isSpeakButtonLongPressed = false;
    private boolean flag = false;
    final private int ADD_TYPE = 0;
    final private int CONTENT_TYPE =1;

    public RecyclerViewAdapter(Context context, List<events.event> events) {
        Log.i(TAG, "RecyclerViewAdapter: " + new Gson().toJson(user));
        this.events= events;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType==CONTENT_TYPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_add_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(position != 0 || !user.getAdmin())
        {
            Log.d(TAG, "onBindViewHolder: called.");
            final events.event event = events.get(position);

            Glide.with(mContext)
                    .asBitmap()
                    .load(event.getImageUrl())
                    .into(holder.image);

            holder.name.setText(event.getTitle());
            switch (position % 4) {
                case 0:
                    holder.constraintLayout.setBackground(mContext.getDrawable(orange));
                    break;
                case 1:
                    holder.constraintLayout.setBackground(mContext.getDrawable(blue));
                    break;
                case 2:
                    holder.constraintLayout.setBackground(mContext.getDrawable(dark_blue));
                    break;
                case 3:
                    holder.constraintLayout.setBackground(mContext.getDrawable(red));
                    break;

            }
           // holder.itemView.setOnLongClickListener(speakHoldListener);
           // holder.itemView.setOnTouchListener(speakTouchListener);
            dummyView.setOnTouchListener(speakTouchListener);
            dummyView.setOnLongClickListener(speakHoldListener);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(user.getAdmin()) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("Delete Subject")
                                .setMessage("Are you sure you want to delete this Event?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                        fStore.collection("events")
                                                .document(event.getVenu() + event.getDate_time()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(mContext, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                                       /* com.example.scrollview.model.events.removeEvent(position);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, EventsActivity.class);
                    if(user.getAdmin())
                      intent.putExtra("position", position+1);
                    else
                        intent.putExtra("position", position);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        List<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
                        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                            ViewHolder holderForAdapterPosition = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                            View itemView = holderForAdapterPosition.image;
                            pairs.add(Pair.create(itemView, "tab_" + i));
                        }
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs.toArray(new Pair[]{})).toBundle();
                        mContext.startActivity(intent, bundle);
                    } else {
                        mContext.startActivity(intent);
                    }
                }


            });
            holder.itemView.setHapticFeedbackEnabled(false);



        }
        else if(user.getAdmin())
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "This is under construction", Toast.LENGTH_SHORT).show();


                    com.example.scrollview.model.events.event addedEvent = new events.event();
                    /*com.example.scrollview.model.events.addEvent(new events.event(),1);*/
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("events").document(addedEvent.getVenu() + addedEvent.getDate_time()).set(addedEvent);





                }
            });
        }

    }

    private View.OnLongClickListener speakHoldListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View pView) {
            // Do something when your hold starts here.
            dummyView.animate()
                    .setDuration(200)
                    .alpha(1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            dummyView.setVisibility(View.VISIBLE);
                        }
                    });
            eventsLayout.animate()
                    .translationY(0)
                    .alpha(1)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationEnd(animation);
                            eventsLayout.setVisibility(View.VISIBLE);
                        }
                    });
            bottomNavigationView.animate()
                    .translationY(bottomNavigationView.getHeight())
                    .alpha(0)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationEnd(animation);
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                    });
            isSpeakButtonLongPressed = true;
            return true;
        }
    };
    private View.OnTouchListener speakTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View pView, MotionEvent pEvent) {
            pView.onTouchEvent(pEvent);
            // We're only interested in when the button is released.
            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                // We're only interested in anything if our speak button is currently pressed.
                if (isSpeakButtonLongPressed) {
                    // Do something when the button is released.
                    eventsLayout.animate()
                            .translationY(eventsLayout.getHeight())
                            .alpha(0)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    eventsLayout.setVisibility(View.GONE);
                                }
                            });
                    dummyView.animate()
                            .setDuration(200)
                            .alpha(0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    dummyView.setVisibility(View.GONE);
                                }
                            });
                    bottomNavigationView.animate()
                            .translationY(0)
                            .alpha(1)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    bottomNavigationView.setVisibility(View.VISIBLE);
                                }
                            });
                    isSpeakButtonLongPressed = false;
                }
            }
            return false;
        }
    };

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0&&user.getAdmin())
            return ADD_TYPE;
        else
            return CONTENT_TYPE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name);
            constraintLayout = itemView.findViewById(R.id.eventLayout);
        }
    }

}
