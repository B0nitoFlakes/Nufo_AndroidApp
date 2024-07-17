package com.example.nufo.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Activities.Recipes.SearchFoodActivity;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {
    String uid;
    TextView textView_home_caloriesConsumed, textView_home_calorieGoal, textView_home_caloriesRemaining;
    Button buttonHome_searchFood;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference, calorieReference, foodLogReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users").child(uid);
        calorieReference = reference.child("personalInfo");
        foodLogReference = reference.child("foodLog");

        buttonHome_searchFood = rootView.findViewById(R.id.buttonHome_searchFood);

        textView_home_caloriesConsumed = rootView.findViewById(R.id.textView_home_caloriesConsumed);
        textView_home_calorieGoal = rootView.findViewById(R.id.textView_home_calorieGoal);
        textView_home_caloriesRemaining = rootView.findViewById(R.id.textView_home_caloriesRemaining);

        buttonHome_searchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchFoodintent = new Intent(getActivity(), SearchFoodActivity.class);
                startActivity(searchFoodintent);
            }
        });

        calorieReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                YourInfoHelperClass yourInfoHelperClass = dataSnapshot.getValue(YourInfoHelperClass.class);
                if (yourInfoHelperClass !=null)
                {
                    double calorieGoal = yourInfoHelperClass.getGoal();

                    textView_home_calorieGoal.setText(String.format(Locale.getDefault(), "%.2f", calorieGoal));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        todayFoodCalories();
        return rootView;
    }
    private void todayFoodCalories()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String todayDate = sdf.format(calendar.getTime());

        // Query Firebase for food entries on today's date
        foodLogReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalCalories = 0.0;
                for (DataSnapshot mealTypeSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dateSnapshot : mealTypeSnapshot.getChildren()) {
                        if (dateSnapshot.getKey().equals(todayDate)) {
                            for (DataSnapshot foodIdSnapshot : dateSnapshot.getChildren()) {
                                DiaryHelperClass foodItem = foodIdSnapshot.getValue(DiaryHelperClass.class);
                                if (foodItem != null) {
                                    totalCalories += foodItem.getCaloriesValue();
                                }
                            }
                        }
                    }
                }
                textView_home_caloriesConsumed.setText(String.format(Locale.getDefault(), "%.2f", totalCalories));
                double goalCalories = Double.parseDouble(textView_home_calorieGoal.getText().toString());
                double remainingCalories = goalCalories - totalCalories;
                textView_home_caloriesRemaining.setText(String.format(Locale.getDefault(), "%.2f", remainingCalories));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

    }

}