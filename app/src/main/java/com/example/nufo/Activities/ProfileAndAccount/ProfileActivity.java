package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView textView_profile_name, textView_profile_age, textView_profile_gender, textView_profile_height, textView_profile_weight, textView_profile_goal;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference, personalReference;
    String uid;

    Button button_update_profile, buttonHome_profile;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        findViews();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users").child(uid);
        personalReference = reference.child("personalInfo");

        dataRetrieval();

        button_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        buttonHome_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void findViews()
    {
        textView_profile_name = findViewById(R.id.textView_profile_name);
        textView_profile_age = findViewById(R.id.textView_profile_age);
        textView_profile_gender = findViewById(R.id.textView_profile_gender);
        textView_profile_height = findViewById(R.id.textView_profile_height);
        textView_profile_weight = findViewById(R.id.textView_profile_weight);
        textView_profile_goal = findViewById(R.id.textView_profile_goal);
        button_update_profile = findViewById(R.id.button_update_profile);

        buttonHome_profile = findViewById(R.id.buttonHome_profile);

    }

    private void dataRetrieval(){

        personalReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                YourInfoHelperClass yourInfoHelperClass = dataSnapshot.getValue(YourInfoHelperClass.class);
                if(yourInfoHelperClass !=null) {
                    String gender = yourInfoHelperClass.getGender();
                    String name = yourInfoHelperClass.getName();
                    String age = String.valueOf(yourInfoHelperClass.getAge());
                    String height = String.valueOf(yourInfoHelperClass.getHeight());
                    String weight = String.valueOf(yourInfoHelperClass.getWeight());
                    double goal = yourInfoHelperClass.getGoal();

                    String formatGoal = String.format("%.2f", goal);

                    textView_profile_name.setText(name);
                    textView_profile_age.setText(age);
                    textView_profile_gender.setText(gender);
                    textView_profile_height.setText(height);
                    textView_profile_weight.setText(weight);
                    textView_profile_goal.setText(formatGoal);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPersonalInfo:onCancelled", error.toException());
            }
        });
    }
}