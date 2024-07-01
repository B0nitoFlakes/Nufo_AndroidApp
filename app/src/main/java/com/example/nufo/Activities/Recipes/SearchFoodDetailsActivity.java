
package com.example.nufo.Activities.Recipes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Adapters.IngredientsAdapter;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Listeners.IngredientDetailsListener;
import com.example.nufo.Listeners.RecipeDetailsListener;
import com.example.nufo.Models.IngredientDetailsResponse;
import com.example.nufo.Models.Nutrient;
import com.example.nufo.Models.RecipeDetailsResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SearchFoodDetailsActivity extends AppCompatActivity {
    String uid;
    int id;
    TextView textView_foodDetails_name, textView_foodDetails_calories, textView_foodDetails_carbohydrates, textView_foodDetails_fats, textView_foodDetails_protein;
    RequestManager manager;
    ProgressDialog dialog;
    Button buttonHome_searchFoodDetails, buttonLogFood, buttonBreakfast, buttonLunch, buttonDinner;
    Dialog logDialog;
    EditText editText_category;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference;
    double caloriesValue = 0.0;
    double carbohydratesValue = 0.0;
    double fatsValue = 0.0;
    double proteinValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food_details);

        setTitle("Food Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        id = Integer.parseInt(getIntent().getStringExtra("id"));
        String type = getIntent().getStringExtra("type");
        Log.d("FoodDetailsActivity", "Received id: " + id);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users").child(uid);


        findViews();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details");
        dialog.show();

        manager = new RequestManager(this);

        if (type != null) {
            if (type.equals("ingredient")) {
                manager.getIngredientDetails(ingredientDetailsListener, id);
            } else if (type.equals("recipe")) {
                manager.getRecipeDetails(recipeDetailsListener, id, true);
            }
        }

        logDialog = new Dialog(SearchFoodDetailsActivity.this);
        logDialog.setContentView(R.layout.log_food_category);
        logDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        logDialog.setCancelable(true);

        editText_category = logDialog.findViewById(R.id.editText_category);
        buttonBreakfast = logDialog.findViewById(R.id.button_category_breakfast);
        buttonLunch = logDialog.findViewById(R.id.button_category_lunch);
        buttonDinner = logDialog.findViewById(R.id.button_category_dinner);

        buttonLogFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDialog.show();
            }
        });

        buttonBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Breakfast");
            }
        });

        buttonLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Lunch");
            }
        });

        buttonDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Dinner");
            }
        });

        buttonHome_searchFoodDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFoodDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void findViews()
    {
        textView_foodDetails_name = findViewById(R.id.textView_foodDetails_name);
        textView_foodDetails_name.setSelected(true);
        textView_foodDetails_calories = findViewById(R.id.textView_foodDetails_calories);
        textView_foodDetails_carbohydrates = findViewById(R.id.textView_foodDetails_carbohydrates);
        textView_foodDetails_fats = findViewById(R.id.textView_foodDetails_fats);
        textView_foodDetails_protein = findViewById(R.id.textView_foodDetails_protein);

        buttonHome_searchFoodDetails = findViewById(R.id.buttonHome_searchFoodDetails);
        buttonLogFood = findViewById(R.id.buttonLogFood);
    }

    private final IngredientDetailsListener ingredientDetailsListener = new IngredientDetailsListener() {
        @Override
        public void didFetch(IngredientDetailsResponse response, String message) {
            dialog.dismiss();
            textView_foodDetails_name.setText(response.name);

            if (response.nutrition != null) {
                Log.d("FoodDetailsActivity", "Nutrition data received");
                for (Nutrient item : response.nutrition.nutrients) {
                    Log.d("FoodDetailsActivity", "Nutrient: " + item.name + " Amount: " + item.amount + " Unit: " + item.unit);
                    switch (item.name) {
                        case "Calories":
                            caloriesValue = item.amount;
                            textView_foodDetails_calories.setText(item.amount + " " + item.unit);
                            break;
                        case "Carbohydrates":
                            carbohydratesValue = item.amount;
                            textView_foodDetails_carbohydrates.setText(item.amount + " " + item.unit);
                            break;
                        case "Fat":
                            fatsValue = item.amount;
                            textView_foodDetails_fats.setText(item.amount + " " + item.unit);
                            break;
                        case "Protein":
                            proteinValue = item.amount;
                            textView_foodDetails_protein.setText(item.amount + " " + item.unit);
                            break;
                    }
                }
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(SearchFoodDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textView_foodDetails_name.setText(response.title);

            if (response.nutrition != null) {
                for (Nutrient item : response.nutrition.nutrients) {
                    switch (item.name) {
                        case "Calories":
                            caloriesValue = item.amount;
                            textView_foodDetails_calories.setText(item.amount + " " + item.unit);
                            break;
                        case "Carbohydrates":
                            carbohydratesValue = item.amount;
                            textView_foodDetails_carbohydrates.setText(item.amount + " " + item.unit);
                            break;
                        case "Fat":
                            fatsValue = item.amount;
                            textView_foodDetails_fats.setText(item.amount + " " + item.unit);
                            break;
                        case "Protein":
                            proteinValue = item.amount;
                            textView_foodDetails_protein.setText(item.amount + " " + item.unit);
                            break;
                    }
                }
            }
        }
        @Override
        public void didError(String message) {
            Toast.makeText(SearchFoodDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private String getCurrentDate() {
        // SimpleDateFormat to get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void logFood(String mealType) {
        String foodName = textView_foodDetails_name.getText().toString();

        String amountString = editText_category.getText().toString().trim();
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalCalories = caloriesValue * amount;
        double totalProtein = proteinValue * amount;
        double totalFats = fatsValue * amount;
        double totalCarbohydrates = carbohydratesValue * amount;

        DiaryHelperClass diaryHelperClass = new DiaryHelperClass(amountString, foodName, totalCalories, totalCarbohydrates, totalProtein, totalFats);

        DatabaseReference foodLogRef = reference.child("foodLog").child(mealType).child(getCurrentDate());

        // Create a new food log entry
        DatabaseReference newFoodRef = foodLogRef.push();
        newFoodRef.setValue(diaryHelperClass).addOnSuccessListener(aVoid ->{
            Toast.makeText(SearchFoodDetailsActivity.this, "Food Logged successfully", Toast.LENGTH_SHORT).show();
        });

        Toast.makeText(this, foodName + " logged for " + mealType, Toast.LENGTH_SHORT).show();
    }
}