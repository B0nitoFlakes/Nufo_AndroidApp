package com.example.nufo.Activities.FoodDiary;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ConcatAdapter;

import com.example.nufo.Activities.FoodRecognition.FoodRecognitionActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Activities.Recipes.RecipeDetailsActivity;
import com.example.nufo.Activities.Recipes.SearchFoodActivity;
import com.example.nufo.Adapters.FoodAdapter;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.example.nufo.Listeners.FoodClickListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FoodDiaryActivity extends AppCompatActivity {
    private String uid, date, selectedMealType;
    private TextView textView_date, textView_diary_caloriesConsumed, textView_diary_calorieGoals, textView_diary_totalFoodCal, textView_diary_caloriesLeft;
    private Button button_diary_breakfast,  button_diary_lunch, button_diary_dinner, buttonHome_foodDiary, button_option_manualSearch, button_option_foodRecognition;
    private RecyclerView recycler_diary_breakfast, recycler_diary_lunch, recycler_diary_dinner;
    private Calendar currentCalendar;
    private ImageButton buttonBack, button_forward;
    private FoodAdapter breakfastAdapter, lunchAdapter, dinnerAdapter;
    private List<DiaryHelperClass> breakfastList = new ArrayList<>();
    private List<DiaryHelperClass> lunchList = new ArrayList<>();
    private List<DiaryHelperClass> dinnerList = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference, calorieReference;
    private Dialog optionDialog;
    private double calorieGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary);

        setTitle("Diary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        findViews();



        breakfastAdapter = new FoodAdapter(breakfastList, FoodDiaryActivity.this, foodClickListener);
        recycler_diary_breakfast.setLayoutManager(new LinearLayoutManager(this));
        recycler_diary_breakfast.setAdapter(breakfastAdapter);

        lunchAdapter = new FoodAdapter(lunchList, FoodDiaryActivity.this, foodClickListener);
        recycler_diary_lunch.setLayoutManager(new LinearLayoutManager(this));
        recycler_diary_lunch.setAdapter(lunchAdapter);

        dinnerAdapter = new FoodAdapter(dinnerList, FoodDiaryActivity.this, foodClickListener);
        recycler_diary_dinner.setLayoutManager(new LinearLayoutManager(this));
        recycler_diary_dinner.setAdapter(dinnerAdapter);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users").child(uid);

        currentCalendar = Calendar.getInstance();
        updateDateDisplay();

        optionDialog = new Dialog(FoodDiaryActivity.this);
        optionDialog.setContentView(R.layout.log_food_options);
        optionDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        optionDialog.setCancelable(true);

        button_option_manualSearch = optionDialog.findViewById(R.id.button_option_manualSearch);
        button_option_foodRecognition = optionDialog.findViewById(R.id.button_option_foodRecognition);

        buttonBack.setOnClickListener(v -> {
            Log.d("FoodDiaryActivity", "buttonBack clicked");
            // Move the calendar back by one day
            currentCalendar.add(Calendar.DAY_OF_YEAR, -1);
            updateDateDisplay();
        });

        button_forward.setOnClickListener(v -> {
            Log.d("FoodDiaryActivity", "buttonBack clicked");
            // Move the calendar up by one day
            currentCalendar.add(Calendar.DAY_OF_YEAR, +1);
            updateDateDisplay();
        });

        button_diary_breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMealType = "Breakfast";
                optionDialog.show();
            }
        });

        button_diary_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMealType = "Lunch";
                optionDialog.show();
            }
        });

        button_diary_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMealType = "Dinner";
                optionDialog.show();
            }
        });

        button_option_manualSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDiaryActivity.this, SearchFoodActivity.class);
                intent.putExtra("mealType", selectedMealType);
                startActivity(intent);
                optionDialog.dismiss();
            }
        });

        button_option_foodRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDiaryActivity.this, FoodRecognitionActivity.class);
                intent.putExtra("mealType", selectedMealType);
                startActivity(intent);
                optionDialog.dismiss();
            }
        });

        buttonHome_foodDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDiaryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        updateDateDisplay();
        if (optionDialog != null && optionDialog.isShowing()) {
            optionDialog.dismiss();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (optionDialog != null && optionDialog.isShowing()) {
            optionDialog.dismiss();
        }
    }
    private void findViews()
    {
        textView_date = findViewById(R.id.textView_date);
        textView_diary_caloriesConsumed = findViewById(R.id.textView_diary_caloriesConsumed);
        textView_diary_calorieGoals = findViewById(R.id.textView_diary_calorieGoals);
        textView_diary_totalFoodCal = findViewById(R.id.textView_diary_totalFoodCal);
        textView_diary_caloriesLeft = findViewById(R.id.textView_diary_caloriesLeft);

        buttonBack = findViewById(R.id.button_back);
        button_forward = findViewById(R.id.button_forward);
        buttonHome_foodDiary = findViewById(R.id.buttonHome_foodDiary);

        button_diary_breakfast = findViewById(R.id.button_diary_breakfast);
        button_diary_lunch = findViewById(R.id.button_diary_lunch);
        button_diary_dinner = findViewById(R.id.button_diary_dinner);

        recycler_diary_breakfast = findViewById(R.id.recycler_diary_breakfast);
        recycler_diary_lunch = findViewById(R.id.recycler_diary_lunch);
        recycler_diary_dinner = findViewById(R.id.recycler_diary_dinner);
    }
    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        date = sdf.format(currentCalendar.getTime());
        textView_date.setText(date);

        if (isToday(currentCalendar)) {
            button_forward.setVisibility(View.INVISIBLE);
        } else {
            button_forward.setVisibility(View.VISIBLE);
        }

        loadCalorieGoal();
        loadFoodData(date);
    }
    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }
    private void loadCalorieGoal() {
        calorieReference = reference.child("personalInfo");
        calorieReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                YourInfoHelperClass yourInfoHelperClass = dataSnapshot.getValue(YourInfoHelperClass.class);
                if (yourInfoHelperClass != null) {
                    calorieGoal = yourInfoHelperClass.getGoal();
                    textView_diary_calorieGoals.setText(String.valueOf(calorieGoal));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FoodDiaryActivity.this, "Error loading calorie goal", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadFoodData(String date) {
        loadMealData("Breakfast", date, breakfastList, breakfastAdapter);
        loadMealData("Lunch", date, lunchList, lunchAdapter);
        loadMealData("Dinner", date, dinnerList, dinnerAdapter);
    }
    private void loadMealData(String mealType, String date, List<DiaryHelperClass> mealList, FoodAdapter adapter) {
        reference.child("foodLog").child(mealType).child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mealList.clear();
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    String foodId = foodSnapshot.getKey();
                    DiaryHelperClass foodItem = foodSnapshot.getValue(DiaryHelperClass.class);

                    foodItem.setId(foodId);
                    foodItem.setDate(date);
                    foodItem.setMealType(mealType);

                    mealList.add(foodItem);
                }
                adapter.notifyDataSetChanged();

                calculateAndDisplayCaloriesInfo();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FoodDiaryActivity.this, "Error" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void calculateAndDisplayCaloriesInfo() {
        double totalCalories = 0.00;
        double goals, calorieRemain;

        for (DiaryHelperClass item : breakfastList) {
            totalCalories += item.getCaloriesValue();
        }
        for (DiaryHelperClass item : lunchList) {
            totalCalories += item.getCaloriesValue();
        }
        for (DiaryHelperClass item : dinnerList) {
            totalCalories += item.getCaloriesValue();
        }

        textView_diary_totalFoodCal.setText(String.format(Locale.getDefault(), "%.2f", totalCalories));
        textView_diary_caloriesConsumed.setText(String.format(Locale.getDefault(), "%.2f", totalCalories));

        goals = calorieGoal;
        calorieRemain = goals - totalCalories;
        textView_diary_calorieGoals.setText(String.format(Locale.getDefault(), "%.2f", goals));
        textView_diary_caloriesLeft.setText(String.format(Locale.getDefault(), "%.2f", calorieRemain));

    }
    private final FoodClickListener foodClickListener = new FoodClickListener() {
        @Override
        public void onFoodClicked(String id, String mealType, String date) {
            Intent intent = new Intent(FoodDiaryActivity.this, FoodDiaryDetailsActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("mealType", mealType);
            intent.putExtra("date", date);
            startActivity(intent);
        }
    };

}