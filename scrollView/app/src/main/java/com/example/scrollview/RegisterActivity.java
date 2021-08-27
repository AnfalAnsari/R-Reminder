package com.example.scrollview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.scrollview.model.User;
import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.example.scrollview.splash_screen.user;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText firstName,email,batch;
    Spinner branch;



    //variables for taking year as input
    String[] type = {"1st year","2nd year","3rd year"} ;
    Spinner spin;
    String[] branchArray ={"Electrical Engineering", "Mechanical Engineering", "Electronics and Communicatin Engineering"};

    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firstName = findViewById(R.id.firstName);
        branch = findViewById(R.id.branch);
        email = findViewById(R.id.emailAddress);
        saveBtn = findViewById(R.id.saveBtn);
        batch = findViewById(R.id.batch);
        spin = findViewById(R.id.year_spinner);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


        //spinner adapter
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_row,type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        //
        ArrayAdapter aa1= new ArrayAdapter(this,R.layout.spinner_row,branchArray);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(aa1);



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstName.getText().toString().isEmpty()||branch.isSelected() || email.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Fill the required Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docRef = fStore.collection("user").document(userID);
               /* Map<String,Object> user = new HashMap<>();
                user.put("name",firstName.getText().toString());
                user.put("branch",branch.getText().toString());
                user.put("email",email.getText().toString());
                user.put("batch",batch.getText().toString());
                user.put("year",spin.getSelectedItem().toString());*/
                 user.setBatch(batch.getText().toString());
                 user.setBranch(branch.getSelectedItem().toString());
                 user.setEmail(email.getText().toString());
                 user.setName(firstName.getText().toString());
                 user.setYear(spin.getSelectedItem().toString());
                 user.setAdmin(false);
                 //add user to database
                 docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User Profile Created." + userID);
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to Create User " + e.toString());
                    }
                });
            }
        });
    }
}