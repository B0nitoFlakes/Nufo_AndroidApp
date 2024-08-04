package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private  String uid;
    private EditText editText_profile_name, editText_profile_age, editText_profile_weight, editText_profile_height;
    private Button button_save_profile, buttonHome_editProfile;
    private RadioGroup radioGroup;
    private Spinner spinner, weightGoalSpinner;
    private String[] gender = {"male", "female"};
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference reference, personalReference;
    private FirebaseUser user;
    private float activityLevel = 1.0f;
    private double goal;
    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        radioGroup = findViewById(R.id.radio_group_activities);
        findViews();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            uid = user.getUid();
            database = FirebaseDatabase.getInstance(); // Initialize the database instance
            reference = database.getReference("users").child(uid);
            personalReference = reference.child("personalInfo");
            populateData();
        } else {
            Log.e(TAG, "User not logged in!");
            // Handle user not logged in, redirect to login page or show a message
        }
        
        reference = database.getReference("users").child(uid);
        personalReference = reference.child("personalInfo");

        populateData();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.r1_edit)
                {
                    updateActivityLevel(1.20f);
                } else if (checkedId == R.id.r2_edit){
                    updateActivityLevel(1.375f);
                } else if (checkedId == R.id.r3_edit) {
                    updateActivityLevel(1.55f);
                } else if (checkedId == R.id.r4_edit) {
                    updateActivityLevel(1.725f);
                } else if (checkedId == R.id.r5_edit) {
                    updateActivityLevel(1.90f);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProfileActivity.this, R.layout.custom_spinner, gender);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
        spinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.weight_goals,
                R.layout.custom_spinner
        );
        goalAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        weightGoalSpinner.setAdapter(goalAdapter);

        button_save_profile.setOnClickListener(view -> saveProfile());

        buttonHome_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private void updateActivityLevel(float level) {
        activityLevel = level;
    }
    private void findViews()
    {
        editText_profile_name = findViewById(R.id.editText_profile_name);
        editText_profile_age = findViewById(R.id.editText_profile_age);
        editText_profile_weight = findViewById(R.id.editText_profile_weight);
        editText_profile_height = findViewById(R.id.editText_profile_height);
        button_save_profile = findViewById(R.id.button_save_profile);
        buttonHome_editProfile = findViewById(R.id.buttonHome_editProfile);
        spinner = findViewById(R.id.genderSpinner);
        weightGoalSpinner = findViewById(R.id.weightGoalSpinner);


    }
    private void populateData()
    {
        personalReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                YourInfoHelperClass yourInfoHelperClass = dataSnapshot.getValue(YourInfoHelperClass.class);
                if(yourInfoHelperClass !=null) {
                    String gender = yourInfoHelperClass.getGender();
                    String weightGoal = yourInfoHelperClass.getWeightGoal();
                    String name = yourInfoHelperClass.getName();
                    String age = String.valueOf(yourInfoHelperClass.getAge());
                    String height = String.valueOf(yourInfoHelperClass.getHeight());
                    String weight = String.valueOf(yourInfoHelperClass.getWeight());
                    float activityLevelDB = yourInfoHelperClass.getActivityLevel();

                    editText_profile_name.setText(name);
                    editText_profile_age.setText(age);
                    editText_profile_height.setText(height);
                    editText_profile_weight.setText(weight);

                    int spinnerPosition = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(gender);
                    spinner.setSelection(spinnerPosition);
                    int spinnerWeightPosition = ((ArrayAdapter<String>) weightGoalSpinner.getAdapter()).getPosition(weightGoal);
                    weightGoalSpinner.setSelection(spinnerWeightPosition);

                    int checkedRadioButtonId = R.id.r1_edit; // Default value
                    if (activityLevelDB == 1.20f) {
                        checkedRadioButtonId = R.id.r1_edit;
                    } else if (activityLevelDB == 1.375f) {
                        checkedRadioButtonId = R.id.r2_edit;
                    } else if (activityLevelDB == 1.55f) {
                        checkedRadioButtonId = R.id.r3_edit;
                    } else if (activityLevelDB == 1.725f) {
                        checkedRadioButtonId = R.id.r4_edit;
                    } else if (activityLevelDB == 1.90f) {
                        checkedRadioButtonId = R.id.r5_edit;
                    }
                    radioGroup.check(checkedRadioButtonId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPersonalInfo:onCancelled", error.toException());
            }
        });
    }
    private void saveProfile() {
        String name = editText_profile_name.getText().toString();
        String age = editText_profile_age.getText().toString();
        String height = editText_profile_height.getText().toString();
        String weight = editText_profile_weight.getText().toString();
        String selectedGender = spinner.getSelectedItem().toString();

        if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float ageValue = Float.parseFloat(age);
            float heightValue = Float.parseFloat(height);
            float weightValue = Float.parseFloat(weight);
            double bmr;
            if (selectedGender.equals("male")) {
                bmr = (weightValue * 10) + (6.25 * heightValue) + 5 - (5 * ageValue);
            } else {
                bmr = (weightValue * 10) + (6.25 * heightValue) - (5 * ageValue) - 161;
            }

            goal = bmr * activityLevel;
            Log.d("BMR_CALC", "Goal before adjustment: " + goal);
            String selectedWeightGoal = ((Spinner) findViewById(R.id.weightGoalSpinner)).getSelectedItem().toString();
            if(selectedWeightGoal.equals("weight loss"))
            {
                goal = goal - 500;
            } else if (selectedWeightGoal.equals("weight gain")) {
                goal = goal + 500;
            }
            Log.d("BMR_CALC", "Goal after adjustment: " + goal);

            YourInfoHelperClass updatedInfo = new YourInfoHelperClass(name, selectedGender, selectedWeightGoal,ageValue, heightValue, weightValue, activityLevel, bmr, goal);

            personalReference.setValue(updatedInfo).addOnSuccessListener(aVoid -> {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numeric values for age, height, and weight", Toast.LENGTH_SHORT).show();
        }
    }


}