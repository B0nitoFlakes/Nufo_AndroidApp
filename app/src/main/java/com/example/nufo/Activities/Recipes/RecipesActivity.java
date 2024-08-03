package com.example.nufo.Activities.Recipes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Adapters.RandomRecipeAdapter;
import com.example.nufo.Listeners.RandomRecipeResponseListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Models.RandomRecipeApiResponse;
import com.example.nufo.R;
import com.example.nufo.RequestManager;

import java.util.ArrayList;
import java.util.List;

public class RecipesActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private RequestManager manager;
    private RandomRecipeAdapter randomRecipeAdapter;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private List<String> tags = new ArrayList<>();
    private SearchView searchView;
    private Button buttonHome_recipesActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        setTitle("Recipes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");

        searchView = findViewById(R.id.searchView_recipe);
        buttonHome_recipesActivity = findViewById(R.id.buttonHome_recipesActivity);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                manager.getRandomRecipe(randomRecipeResponseListener, tags);
                dialog.show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner = findViewById(R.id.spinner_tags);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListener);

        manager = new RequestManager(this);

        buttonHome_recipesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(RecipesActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(RecipesActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipesActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            tags.clear();
            tags.add(adapterView.getSelectedItem().toString());
            manager.getRandomRecipe(randomRecipeResponseListener, tags);
            dialog.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecipesActivity.this, RecipeDetailsActivity.class)
                    .putExtra("id", id));
        }
    };
}