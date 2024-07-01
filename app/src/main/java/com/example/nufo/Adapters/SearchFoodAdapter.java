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

import com.example.nufo.Listeners.FoodItem;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.Listeners.SearchIngredientListener;
import com.example.nufo.Models.Result;
import com.example.nufo.R;

import java.util.List;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodViewHolder>{
    Context context;
    List<Result> list;
    RecipeClickListener listener;

    public SearchFoodAdapter(Context context, List<Result> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchFoodViewHolder(LayoutInflater.from(context).inflate(R.layout.search_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFoodViewHolder holder, int position) {
        holder.textView_searchFood_name.setText(list.get(position).name);
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

class SearchFoodViewHolder extends RecyclerView.ViewHolder{
    TextView textView_searchFood_name;
    CardView random_list_container_searchFood;

    public SearchFoodViewHolder(@NonNull View itemView) {
        super(itemView);

        random_list_container_searchFood = itemView.findViewById(R.id.random_list_container_searchFood);
        textView_searchFood_name = itemView.findViewById(R.id.textView_searchFood_name);
    }
}
