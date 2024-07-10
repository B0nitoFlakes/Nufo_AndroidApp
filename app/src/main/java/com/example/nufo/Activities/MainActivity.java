package com.example.nufo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.ProfileAndAccount.ProfileActivity;
import com.example.nufo.Activities.Recipes.RecipeDetailsActivity;
import com.example.nufo.Activities.Recipes.RecipesActivity;
import com.example.nufo.Activities.Recipes.SearchFoodActivity;
import com.example.nufo.Adapters.HomeRecipesAdapter;
import com.example.nufo.Adapters.RandomRecipeAdapter;
import com.example.nufo.Fragments.HomeFragment;
import com.example.nufo.Listeners.RandomRecipeResponseListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Models.RandomRecipeApiResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;
import com.example.nufo.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RequestManager manager;
    RecyclerView recycler_home_recipe;
    ActivityMainBinding binding;
    List<String> tags = new ArrayList<>();
    HomeRecipesAdapter homeRecipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

                case R.id.more:
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
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


