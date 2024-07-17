package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.R;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class YourInformationActivity extends AppCompatActivity {

    String[] gender = {"male", "female"};
    EditText nameA, heightA, weightA, ageA;
    Button doneInput;
    float activityLevel = 1.0f; // Default activity level

    double bmr, goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_information);

        heightA = findViewById(R.id.heightEditText);
        weightA = findViewById(R.id.weightEditText);
        ageA = findViewById(R.id.ageEditText);
        nameA = findViewById(R.id.nameEditText);
        doneInput = findViewById(R.id.donePersonalInfoButton);
        Spinner spinner = findViewById(R.id.genderSpinner);
        Spinner goalSpinner = findViewById(R.id.weightGoalSpinner);

        RadioButton r1 = findViewById(R.id.r1);
        RadioButton r2 = findViewById(R.id.r2);
        RadioButton r3 = findViewById(R.id.r3);
        RadioButton r4 = findViewById(R.id.r4);
        RadioButton r5 = findViewById(R.id.r5);

        r1.setOnCheckedChangeListener((compoundButton, isChecked) -> updateActivityLevel(1.20f));
        r2.setOnCheckedChangeListener((compoundButton, isChecked) -> updateActivityLevel(1.375f));
        r3.setOnCheckedChangeListener((compoundButton, isChecked) -> updateActivityLevel(1.55f));
        r4.setOnCheckedChangeListener((compoundButton, isChecked) -> updateActivityLevel(1.725f));
        r5.setOnCheckedChangeListener((compoundButton, isChecked) -> updateActivityLevel(1.90f));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(YourInformationActivity.this, R.layout.custom_spinner, gender);
        adapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(adapter);


        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.weight_goals,
                R.layout.custom_spinner
        );
        goalAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        goalSpinner.setAdapter(goalAdapter);

        doneInput.setOnClickListener(view -> calculateBMR());
    }

    private void updateActivityLevel(float level) {
        activityLevel = level;
    }

    private  void updateGoal(double endGoal){
        goal = endGoal;
    }


    private void calculateBMR() {
        try {
            String name = nameA.getText().toString();
            float height = Float.parseFloat(heightA.getText().toString());
            float weight = Float.parseFloat(weightA.getText().toString());
            float age = Float.parseFloat(ageA.getText().toString());

            String selectedGender = gender[0]; // Default to male if nothing selected

            if (findViewById(R.id.genderSpinner) != null) {
                selectedGender = ((Spinner) findViewById(R.id.genderSpinner)).getSelectedItem().toString();
            }

            if (selectedGender.equals("male")) {
                bmr = (weight * 10) + (6.25 * height) + 5 - (5 * age);
            } else if (selectedGender.equals("female")) {
                bmr = (weight * 10) + (6.25 * height) - (5 * age) - 161;
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

            YourInfoHelperClass yourInfoHelperClass = new YourInfoHelperClass(name, selectedGender, selectedWeightGoal, age, height, weight, activityLevel, bmr, goal);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            reference.child("personalInfo").setValue(yourInfoHelperClass).addOnSuccessListener(aVoid ->{
                Toast.makeText(YourInformationActivity.this, "Personal information saved successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(YourInformationActivity.this, LoginActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(YourInformationActivity.this, "Failed to save personal information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });



        } catch (NumberFormatException e) {
            Toast.makeText(YourInformationActivity.this, "Please enter valid numeric values for height, weight, and age", Toast.LENGTH_SHORT).show();
        }
    }
}
