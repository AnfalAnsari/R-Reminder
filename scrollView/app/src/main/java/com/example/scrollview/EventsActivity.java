package com.example.scrollview;


import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.example.scrollview.model.events;
import com.google.gson.Gson;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import java.util.Calendar;
import java.util.Locale;
import static com.example.scrollview.model.events.getCategories;

public class EventsActivity extends AppCompatActivity {

    private PageIndicator indicator;
    private static final String TAG = "EventsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // getDelegate().setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new IconAdapter(this, getSupportFragmentManager()));
        pager.setPageTransformer(true, new ZoomAnimation());
        indicator = (PageIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(pager);
        pager.setCurrentItem(getIntent().getIntExtra("position", 0)-1, false);
        supportPostponeEnterTransition();
        pager.post(new Runnable() {
            @Override
            public void run() {
                supportStartPostponedEnterTransition();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class IconAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {
        private final Context context;

        private IconAdapter(Context context, FragmentManager manager) {
            super(manager);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return DetailPage.newInstance(context,position);
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }

        @Override
        public int getCount() {
            return events.getCategories().size();
        }
    }

    public static class DetailPage extends Fragment {
        ImageView posterview;
        TextView title;
        TextView description;
        Button remindMe;
        TextView location;
        TextView date_time;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootview = inflater.inflate(R.layout.detail_page, container, false);
             posterview = (ImageView) rootview.findViewById(R.id.event_poster);
             title = (TextView) rootview.findViewById(R.id.event_title);
             description = (TextView) rootview.findViewById(R.id.event_desc);
             remindMe = (Button) rootview.findViewById(R.id.remind_button);
             location = rootview.findViewById(R.id.event_location);
             date_time = rootview.findViewById(R.id.event_dateTime);

             //imageView=(ImageView) rootview.findViewById(R.id.)
            String posterUrl=null;
             events.event event1= new events.event();
            if (getArguments() != null) {
                String jsonString;
                jsonString = getArguments().getString("events");
                Gson gson= new Gson();
                event1=gson.fromJson(jsonString,event1.getClass());
                title.setText(event1.getTitle());
                description.setText(event1.getDescription());
                location.setText(event1.getVenu());
                String myFormat = "E, dd MMM yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                Calendar eventCalendar =  Calendar.getInstance();
                eventCalendar.setTime(event1.getDate_time().toDate());
                String Date_Time = sdf.format(eventCalendar.getTime())+" "+ updateTime(eventCalendar.get(Calendar.HOUR_OF_DAY),eventCalendar.get(Calendar.MINUTE));
                date_time.setText(Date_Time);
               ;

            }
            Glide.with(getContext())
                    .asBitmap()
                    .load(event1.getPosterUrl())
                    .into(posterview);
           /* Glide.with(getContext())
                    .asBitmap()
                    .load(event.getImageUrl())
                    .into(imageView)*/
            remindMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jsonString = getArguments().getString("events");
                    Log.i(TAG, "onClick: " + jsonString);
                    Intent intent = new Intent(getActivity(),reminderActivity.class);
                    intent.putExtra("events_info",jsonString);
                    startActivity(intent);

                }
            });


            return rootview;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
            list.setLayoutManager(new LinearLayoutManager(getContext()));

            super.onViewCreated(view, savedInstanceState);

        }

        public static DetailPage newInstance(Context context,int index) {
            DetailPage detailPage = new DetailPage();
            Bundle args = new Bundle();
            Gson gson= new Gson();
            String json=gson.toJson(getCategories().get(index));
            args.putString("events", json);
            Log.i(TAG, "newInstance: " +  index + " " + json);
            detailPage.setArguments(args);
            return detailPage;
        }

        /*private static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView title;


            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
            }
        }

        */
        public static class Item {
            public final String title;

            private Item(String title) {
                this.title = title;
            }
        }
        /*

         public static class Adapter extends RecyclerView.Adapter<ViewHolder> {
            private LayoutInflater inflater;
            private final List<Item> items;

            public Adapter(LayoutInflater inflater, List<Item> items) {
                this.inflater = inflater;
                this.items = items;
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.detail_list_item, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.title.setText(items.get(position).title);
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        }*/
        private String updateTime(int hours, int mins) {


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

            return aTime;
        }



    }


}
