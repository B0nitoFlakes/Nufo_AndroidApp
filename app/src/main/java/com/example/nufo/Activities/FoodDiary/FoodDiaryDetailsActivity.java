package com.example.nufo.Activities.FoodDiary;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FoodDiaryDetailsActivity extends AppCompatActivity {
    private String uid, mealType, dateString;
    private TextView textView_foodDiaryDetails_name, textView_foodDiaryDetails_calories, textView_foodDiaryDetails_carbohydrates, textView_foodDiaryDetails_fats, textView_foodDiaryDetails_protein, textView_foodDiaryDetails_amount;
    private String id;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference foodRef;
    private Button buttonHome_foodDiaryDetails, button_yes, button_no, buttonDeleteFood;
    private Dialog deleteDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_diary_details);

        setTitle("Food Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        id = getIntent().getStringExtra("id");
        mealType = getIntent().getStringExtra("mealType");
        dateString = getIntent().getStringExtra("date");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();

        findViews();
        loadMealData(id);
        foodDeletion();

        buttonHome_foodDiaryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodDiaryDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private void findViews()
    {
        textView_foodDiaryDetails_name = findViewById(R.id.textView_foodDiaryDetails_name);
        textView_foodDiaryDetails_calories = findViewById(R.id.textView_foodDiaryDetails_calories);
        textView_foodDiaryDetails_carbohydrates = findViewById(R.id.textView_foodDiaryDetails_carbohydrates);
        textView_foodDiaryDetails_fats = findViewById(R.id.textView_foodDiaryDetails_fats);
        textView_foodDiaryDetails_protein = findViewById(R.id.textView_foodDiaryDetails_protein);
        textView_foodDiaryDetails_amount = findViewById(R.id.textView_foodDiaryDetails_amount);
        buttonHome_foodDiaryDetails = findViewById(R.id.buttonHome_foodDiaryDetails);
        buttonDeleteFood = findViewById(R.id.buttonDeleteFood);
    }
    private void loadMealData(String id)
    {
        if(id !=null)
        {
            foodRef = database.getReference("users").child(uid).child("foodLog").child(mealType).child(dateString).child(id);
            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DiaryHelperClass foodItem = snapshot.getValue(DiaryHelperClass.class);
                    if(foodItem !=null)
                    {
                        String foodName = foodItem.getFoodName();
                        String amount = foodItem.getAmount();
                        String caloriesValue = String.valueOf(foodItem.getCaloriesValue());
                        String carbohydratesValue = String.valueOf(foodItem.getCarbohydratesValue());
                        String fatsValue = String.valueOf(foodItem.getFatsValue());
                        String proteinValue = String.valueOf(foodItem.getProteinValue());

                        textView_foodDiaryDetails_amount.setText(amount);
                        textView_foodDiaryDetails_name.setText(foodName);
                        textView_foodDiaryDetails_calories.setText(caloriesValue);
                        textView_foodDiaryDetails_carbohydrates.setText(carbohydratesValue);
                        textView_foodDiaryDetails_fats.setText(fatsValue);
                        textView_foodDiaryDetails_protein.setText(proteinValue);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(FoodDiaryDetailsActivity.this, "Error" , Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(FoodDiaryDetailsActivity.this, "No Food Details Available" , Toast.LENGTH_SHORT).show();
        }
    }
    private void foodDeletion()
    {
        deleteDialog = new Dialog(FoodDiaryDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_food);
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        deleteDialog.setCancelable(true);

        button_yes = deleteDialog.findViewById(R.id.button_yes);
        button_no = deleteDialog.findViewById(R.id.button_no);

        buttonDeleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.show();
            }
        });

        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FoodDiaryDetailsActivity.this, "Food item deleted successfully", Toast.LENGTH_SHORT).show();
                        deleteDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(FoodDiaryDetailsActivity.this, "Failed to delete food item", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });
    }


}