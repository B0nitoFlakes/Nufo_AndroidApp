package com.example.nufo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Models.Recipe;
import com.example.nufo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRecipesAdapter extends RecyclerView.Adapter<HomeRecipesViewHolder> {

    Context context;
    List<Recipe> list;
    RecipeClickListener listener;

    public HomeRecipesAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeRecipesViewHolder(LayoutInflater.from(context).inflate(R.layout.home_recipes, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecipesViewHolder holder, int position) {

        holder.textView_home_recipe.setText(list.get(position).title);
        holder.textView_home_recipe.setSelected(true);
        Picasso.get().load(list.get(position).image).into(holder.imageView_home_food);

        holder.home_recipe_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}

class HomeRecipesViewHolder extends RecyclerView.ViewHolder{
    CardView home_recipe_container;
    TextView textView_home_recipe;
    ImageView imageView_home_food;

    public HomeRecipesViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView_home_food = itemView.findViewById(R.id.imageView_home_food);
        textView_home_recipe = itemView.findViewById(R.id.textView_home_recipe);
        home_recipe_container = itemView.findViewById(R.id.home_recipe_container);

    }
}