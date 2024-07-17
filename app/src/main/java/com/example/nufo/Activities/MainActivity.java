package com.example.nufo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.ProfileAndAccount.SettingsActivity;
import com.example.nufo.Activities.Recipes.RecipeDetailsActivity;
import com.example.nufo.Activities.Recipes.RecipesActivity;
import com.example.nufo.Activities.Recipes.SearchFoodActivity;
import com.example.nufo.Adapters.HomeRecipesAdapter;
import com.example.nufo.Fragments.HomeFragment;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Helpers.YourInfoHelperClass;
import com.example.nufo.Listeners.RandomRecipeResponseListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Models.RandomRecipeApiResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;
import com.example.nufo.databinding.ActivityMainBinding;
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

public class MainActivity extends AppCompatActivity {
    String uid;
    RequestManager manager;
    RecyclerView recycler_home_recipe;
    ActivityMainBinding binding;
    List<String> tags = new ArrayList<>();
    HomeRecipesAdapter homeRecipesAdapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.show();

        manager = new RequestManager(this);
        manager.getRandomRecipe(randomRecipeResponseListener, tags);

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()){
                case R.id.home:
                    Intent searchFoodintent = new Intent(MainActivity.this, SearchFoodActivity.class);
                    startActivity(searchFoodintent);
                    break;

                case R.id.recipe:
                    Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                    startActivity(intent);
                    break;

                case R.id.foodDiary:
                    Intent diaryIntent = new Intent(MainActivity.this, FoodDiaryActivity.class);
                    startActivity(diaryIntent);
                    break;

                case R.id.settings:
                    Intent profileIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(profileIntent);
                    break;

            }

            return true;

        });

    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recycler_home_recipe = findViewById(R.id.recycler_home_recipe);
            recycler_home_recipe.setHasFixedSize(true);
            recycler_home_recipe.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            homeRecipesAdapter = new HomeRecipesAdapter(MainActivity.this, response.recipes, recipeClickListener);
            recycler_home_recipe.setAdapter(homeRecipesAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(MainActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id));
        }
    };

}


