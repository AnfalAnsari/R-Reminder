package com.example.scrollview.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.scrollview.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class events {
    private static List<event> categories = new ArrayList<>();
    private static final String TAG = "events";
    public static List<event> getCategories() {
        return categories;
    }
    public static void setCategories(List<event> events)
    {
        categories = events;
    }
    public static void addEvent(event event , int index)
    {
        categories.add(index,event);
        Log.i(TAG, "addEvent: " + new Gson().toJson(categories));
    }
    public static int getSize()
    {
        return categories.size();
    }


    public static void removeEvent(int index)
    {
        categories.remove(index);
    }

    private static List<event> generateItems() {
        final List<event> items = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              Gson gson = new Gson();

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<event> events = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            events.add(document.toObject(events.event.class));
                        }

                        Log.i(TAG, "onComplete: events" + gson.toJson(events.get(0)));
                        Log.i(TAG, "onComplete: events" + gson.toJson(events.get(1)));
                        items.addAll(events);

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });

            for (int i = 0; i < 2; i++) {
            items.add(new event(i));

        }

              return items;

    }
    public List<event> geteventlist()
    {
        return categories;
    }


    public static class event {
        public final String title;
        public final String imageUrl;
        public final Timestamp date_time;
        public final String description;
        public final String posterUrl;
        public final String venu;


        private event(String title, String imageUrl, Timestamp date_time, String description, String posterUrl, String venu) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.date_time = date_time;
            this.description = description;
            this.posterUrl = posterUrl;
            this.venu = venu;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Timestamp getDate_time() {
            return date_time;
        }

        public String getDescription() {
            return description;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public String getVenu() {
            return venu;
        }

        public event(int i) {
            this.date_time = new Timestamp(new Date());
            this.imageUrl ="";
            this.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et " +
                    "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea " +
                    "commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla " +
                    "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
            this.posterUrl = " ";
            this.title = "";
            this.venu = "MAC Audi";

        }
        public event(){
            this.date_time = new Timestamp(new Date());
            this.imageUrl = "";
            this.description = "";
            this.posterUrl = "";
            this.title = "Dummy Event";
            this.venu = "MAC AUDi";
        }


    }



}
