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

public class SearchFoodRecipeAdapter extends RecyclerView.Adapter<SearchFoodRecipeViewHolder>{
    Context context;
    List<Recipe> list;
    RecipeClickListener listener;

    public SearchFoodRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchFoodRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchFoodRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.search_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFoodRecipeViewHolder holder, int position) {
        holder.textView_searchFood_name.setText(list.get(position).title);
        holder.textView_searchFood_name.setSelected(true);

        holder.random_list_container_searchFood.setOnClickListener(new View.OnClickListener() {
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
class SearchFoodRecipeViewHolder extends RecyclerView.ViewHolder {
    TextView textView_searchFood_name;
    CardView random_list_container_searchFood;

    public SearchFoodRecipeViewHolder(@NonNull View itemView) {
        super(itemView);

        random_list_container_searchFood = itemView.findViewById(R.id.random_list_container_searchFood);
        textView_searchFood_name = itemView.findViewById(R.id.textView_searchFood_name);

    }
}

