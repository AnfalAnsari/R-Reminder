package com.example.scrollview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrollview.model.Subject;
import com.example.scrollview.model.Tasks;
import com.example.scrollview.model.User;
import com.example.scrollview.model.events;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.scrollview.model.events.setCategories;
import static com.example.scrollview.splash_screen.subjects;
import static com.example.scrollview.splash_screen.user;
import static com.example.scrollview.model.events.getCategories;
import static com.example.scrollview.splash_screen.user;


public class HomeFragment extends Fragment {
    public static TaskRecyclerViewAdapter taskAdapter;
    //vars
    private ArrayList<String> mTasks = new ArrayList<>();
    private ArrayList<String> mTaskImages = new ArrayList<>();
    private ArrayList<String> mTaskDate = new ArrayList<>();
    public static List<Tasks> savedTasks;
    static LinearLayoutManager layoutManager;
    RecyclerView taskRecyclerView;
    public static RecyclerView recyclerView;
    public static TextView emptyView;
    TextView name;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = rootView.findViewById(R.id.events);
        emptyView = rootView.findViewById(R.id.empty_view);
        name=rootView.findViewById(R.id.Name);
        name.setText("Hi "+ user.getName());
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), getCategories());





        recyclerView.setAdapter(adapter);
        taskRecyclerView = rootView.findViewById(R.id.taskList);
        mTaskImages.add("https://www.iitr.ac.in/nss/images/nss_iitr_logo.png");
        mTasks.add("NSS BDC");
        mTaskDate.add("11 Aug 6:00");

        mTaskImages.add("https://img.channeli.in/static/images/imglogo.png");
        mTasks.add("IMG Talk");
        mTaskDate.add("11 Aug 6:00");

        mTaskImages.add("data:image/png;base64,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");
        mTasks.add("Finance design recruitment");
        mTaskDate.add("11 Aug 6:00");

        mTaskImages.add("https://www.iitr.ac.in/nss/images/nss_iitr_logo.png");
        mTasks.add("NSS BDC");
        mTaskDate.add("11 Aug 6:00");

        mTaskImages.add("https://img.channeli.in/static/images/imglogo.png");
        mTasks.add("IMG Talk");
        mTaskDate.add("11 Aug 6:00");
        savedTasks= new ArrayList<>();
        savedTasks= getList();
        final LinearLayoutManager taskLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        taskRecyclerView.setLayoutManager(taskLayout);
        taskAdapter = new TaskRecyclerViewAdapter(getContext(),savedTasks);
        taskRecyclerView.setAdapter(taskAdapter);
        DocumentReference userDocRef = fStore.collection("user").document(fAuth.getCurrentUser().getUid());
        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                try {

                    Log.i(TAG, "onEvent: listener called");
                    if (documentSnapshot != null && documentSnapshot.exists())
                    {
                        User new_user;
                        new_user = documentSnapshot.toObject(user.getClass());
                        if (new_user.getAdmin() != user.getAdmin()) {
                            if (new_user.getAdmin()) {
                                getCategories().add(0, new events.event());
                            } else {
                                getCategories().remove(0);
                            }
                        }
                        user = new_user;
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }

            }
        });
        final Query eventColRef = fStore.collection("events").orderBy("date_time");
        eventColRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.i("TAG", "listen:error", e);
                    return;
                }

                List<events.event> temp = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                {
                    temp.add(doc.toObject(events.event.class));
                }
                if(user.getAdmin())
                {
                    temp.add(0,new events.event());
                }
                getCategories().clear();
                getCategories().addAll(temp);
                adapter.notifyDataSetChanged();



            }
        });


        Log.i(TAG, "onCreateView: events" + new Gson().toJson(getCategories()));
        return rootView;
    }

    private List<Tasks> getList() {
        List<Tasks> arrayItems;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.example.scrollview",Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString("tasks", null);

        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tasks>>(){}.getType();
            arrayItems = gson.fromJson(serializedObject, type);
            if(arrayItems.size()==0)
            {
                emptyView.setVisibility(View.VISIBLE);
            }
            else if(emptyView.getVisibility()==View.VISIBLE)
            {
                emptyView.setVisibility(View.GONE);
            }

            return arrayItems;
        }
        else
        {   List<Tasks> dTasks = new ArrayList<>();
            emptyView.setVisibility(View.VISIBLE);
            return new ArrayList<>();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
    }
}
