package com.example.nufo.Activities.Recipes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Activities.FoodDiary.FoodDiaryActivity;
import com.example.nufo.Activities.FoodRecognition.FoodRecognitionActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Adapters.RandomRecipeAdapter;
import com.example.nufo.Adapters.SearchFoodAdapter;
import com.example.nufo.Adapters.SearchFoodRecipeAdapter;
import com.example.nufo.Listeners.RandomRecipeResponseListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Listeners.SearchIngredientListener;
import com.example.nufo.Models.RandomRecipeApiResponse;
import com.example.nufo.Models.SearchIngredientApiResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;

import java.util.ArrayList;
import java.util.List;

public class SearchFoodActivity extends AppCompatActivity {
    private String mealType;
    private Button buttonHome_searchFoodActivity;
    private ProgressDialog dialog;
    private RequestManager manager;
    private RecyclerView recyclerView, recycler_searchFoodRandom;
    private SearchView searchView;
    private SearchFoodAdapter adapter;
    private SearchFoodRecipeAdapter searchFoodRecipeAdapter;
    private List<String> tags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        setTitle("Food Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);


        recyclerView = findViewById(R.id.recycler_searchFood);
        searchView = findViewById(R.id.searchView_searchFood);
        recycler_searchFoodRandom = findViewById(R.id.recycler_searchFoodRandom);
        buttonHome_searchFoodActivity = findViewById(R.id.buttonHome_searchFoodActivity);
        mealType = getIntent().getStringExtra("mealType");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        manager = new RequestManager(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.show();
                tags.clear();
                tags.add(query);
                manager.searchIngredient(searchIngredientListener, query);
                manager.getRandomRecipe(randomRecipeResponseListener, tags);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        buttonHome_searchFoodActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFoodActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private final SearchIngredientListener searchIngredientListener = new SearchIngredientListener() {
        @Override
        public void didFetch(SearchIngredientApiResponse response, String message) {
            dialog.dismiss();
            recyclerView.setLayoutManager(new LinearLayoutManager(SearchFoodActivity.this));
            adapter = new SearchFoodAdapter(SearchFoodActivity.this, response.results, recipeClickListener);
            recyclerView.setAdapter(adapter);
        }
        @Override
        public void didError(String message) {
            dialog.dismiss();
            Toast.makeText(SearchFoodActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recycler_searchFoodRandom.setLayoutManager(new LinearLayoutManager(SearchFoodActivity.this));
            recycler_searchFoodRandom.setHasFixedSize(true);
            searchFoodRecipeAdapter = new SearchFoodRecipeAdapter(SearchFoodActivity.this, response.recipes, recipeClickListener1);
            recycler_searchFoodRandom.setAdapter(searchFoodRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(SearchFoodActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(SearchFoodActivity.this, SearchFoodDetailsActivity.class)
                    .putExtra("id", id)
                    .putExtra("type", "ingredient")
                    .putExtra("mealType", mealType));
        }
    };
    private final RecipeClickListener recipeClickListener1 = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(SearchFoodActivity.this, SearchFoodDetailsActivity.class)
                    .putExtra("id", id)
                    .putExtra("type", "recipe")
                    .putExtra("mealType", mealType));
        }
    };
}