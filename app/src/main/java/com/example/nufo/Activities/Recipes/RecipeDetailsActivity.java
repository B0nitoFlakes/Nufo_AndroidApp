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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.FoodRecognition.FoodRecognitionActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Adapters.IngredientsAdapter;
import com.example.nufo.Adapters.InstructionsAdapter;
import com.example.nufo.Adapters.SimilarRecipeAdapter;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Listeners.InstructionsListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Listeners.RecipeDetailsListener;
import com.example.nufo.Listeners.SimilarRecipeListener;
import com.example.nufo.Models.InstructionsResponse;
import com.example.nufo.Models.Nutrient;
import com.example.nufo.Models.RecipeDetailsResponse;
import com.example.nufo.Models.SimilarRecipeResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecipeDetailsActivity extends AppCompatActivity {
    private String uid;
    private int id;
    private TextView textView_meal_name, textView_meal_source;
    private TextView textView_calories, textView_carbohydrates, textView_fats, textView_protein;
    private ImageView imageView_meal_image;
    private RecyclerView recycler_meal_ingredients, recycler_meal_similar, recycler_meal_instructions;
    private Button buttonLogFood, buttonBreakfast, buttonLunch, buttonDinner,buttonDone, buttonHome_recipeDetails;
    private EditText editText_category;
    private RequestManager manager;
    private ProgressDialog dialog;
    private Dialog logDialog;
    private IngredientsAdapter ingredientsAdapter;
    private SimilarRecipeAdapter similarRecipeAdapter;
    private InstructionsAdapter instructionsAdapter;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    private double caloriesValue = 0.0;
    private double carbohydratesValue = 0.0;
    private double fatsValue = 0.0;
    private double proteinValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        setTitle("Recipe Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        findViews();

        id = Integer.parseInt(getIntent().getStringExtra("id"));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uid = auth.getUid();
        reference = database.getReference("users").child(uid);

        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener, id, true);
        manager.getSimilarRecipes(similarRecipeListener, id);
        manager.getInstructions(instructionsListener, id);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details");
        dialog.show();

        buttonLogFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDialog.show();
                buttonDone.setVisibility(View.GONE);
            }
        });
        buttonBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Breakfast");
                Intent intent = new Intent(RecipeDetailsActivity.this, FoodDiaryActivity.class);
                startActivity(intent);
            }
        });
        buttonLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Lunch");
                Intent intent = new Intent(RecipeDetailsActivity.this, FoodDiaryActivity.class);
                startActivity(intent);
            }
        });
        buttonDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logFood("Dinner");
                Intent intent = new Intent(RecipeDetailsActivity.this, FoodDiaryActivity.class);
                startActivity(intent);
            }
        });

        buttonHome_recipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeDetailsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar = findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions = findViewById(R.id.recycler_meal_instructions);

        textView_calories = findViewById(R.id.textView_calories);
        textView_carbohydrates = findViewById(R.id.textView_carbohydrates);
        textView_fats = findViewById(R.id.textView_fats);
        textView_protein = findViewById(R.id.textView_protein);

        buttonLogFood = findViewById(R.id.buttonLogFood);
        buttonHome_recipeDetails = findViewById(R.id.buttonHome_recipeDetails);

        logDialog = new Dialog(RecipeDetailsActivity.this);
        logDialog.setContentView(R.layout.log_food_category);
        logDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        logDialog.setCancelable(true);

        editText_category = logDialog.findViewById(R.id.editText_category);
        buttonBreakfast = logDialog.findViewById(R.id.button_category_breakfast);
        buttonLunch = logDialog.findViewById(R.id.button_category_lunch);
        buttonDinner = logDialog.findViewById(R.id.button_category_dinner);
        buttonDone = logDialog.findViewById(R.id.button_category_done);
    }
    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textView_meal_name.setText(response.title);
            textView_meal_source.setText(response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);

            if (response.nutrition != null) {
                for (Nutrient item : response.nutrition.nutrients) {
                    switch (item.name) {
                        case "Calories":
                            caloriesValue = item.amount;
                            textView_calories.setText(item.amount + " " + item.unit);
                            break;
                        case "Carbohydrates":
                            carbohydratesValue = item.amount;
                            textView_carbohydrates.setText(item.amount + " " + item.unit);
                            break;
                        case "Fat":
                            fatsValue = item.amount;
                            textView_fats.setText(item.amount + " " + item.unit);
                            break;
                        case "Protein":
                            proteinValue = item.amount;
                            textView_protein.setText(item.amount + " " + item.unit);
                            break;
                    }
                }
            }
        }
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final SimilarRecipeListener similarRecipeListener = new SimilarRecipeListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipeDetailsActivity.this, response, recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);
        }
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecipeDetailsActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id));
        }
    };
    private String getCurrentDate() {
        // SimpleDateFormat to get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void logFood(String mealType) {
        String foodName = textView_meal_name.getText().toString();
        String amountString = editText_category.getText().toString().trim();
        double amount;

        if (amountString.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(RecipeDetailsActivity.this, "Food Logged successfully", Toast.LENGTH_SHORT).show();
        });

        Toast.makeText(this, foodName + " logged for " + mealType, Toast.LENGTH_SHORT).show();
    }
}