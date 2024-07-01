package com.example.nufo.Activities.Recipes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    ProgressDialog dialog;
    RequestManager manager;
    RecyclerView recyclerView, recycler_searchFoodRandom;
    SearchView searchView;
    SearchFoodAdapter adapter;
    SearchFoodRecipeAdapter searchFoodRecipeAdapter;
    RandomRecipeAdapter randomRecipeAdapter;
    List<String> tags = new ArrayList<>();

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

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        manager = new RequestManager(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchFoodAdapter(this, new ArrayList<>(), null);
        recyclerView.setAdapter(adapter);

        recycler_searchFoodRandom.setLayoutManager(new LinearLayoutManager(this));
        searchFoodRecipeAdapter = new SearchFoodRecipeAdapter(this, new ArrayList<>(), null);
        recycler_searchFoodRandom.setAdapter(searchFoodRecipeAdapter);

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
    }

    private final SearchIngredientListener searchIngredientListener = new SearchIngredientListener() {
        @Override
        public void didFetch(SearchIngredientApiResponse response, String message) {
            dialog.dismiss();
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
            recycler_searchFoodRandom.setHasFixedSize(true);
//            recycler_searchFoodRandom.setLayoutManager(new GridLayoutManager(SearchFoodActivity.this, 1));
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
                    .putExtra("type", "ingredient"));
        }
    };

    private final RecipeClickListener recipeClickListener1 = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(SearchFoodActivity.this, SearchFoodDetailsActivity.class)
                    .putExtra("id", id)
                    .putExtra("type", "recipe"));
        }
    };
}