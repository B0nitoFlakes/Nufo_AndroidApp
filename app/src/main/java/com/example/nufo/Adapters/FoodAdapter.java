package com.example.nufo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Listeners.FoodClickListener;
import com.example.nufo.Listeners.RecipeClickListener;
import com.example.nufo.R;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapterViewHolder>{
    Context context;
    List<DiaryHelperClass> list;
    FoodClickListener listener;

    public FoodAdapter(List<DiaryHelperClass> list, Context context, FoodClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public FoodAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoodAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.list_food_diary, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapterViewHolder holder, int position) {
        holder.textView_list_diary.setText(list.get(position).getFoodName());
        Log.d("FoodAdapter", "Food name: " + list.get(position).getFoodName());
        holder.textView_list_diary.setSelected(true);

        holder.cardView_list_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFoodClicked(list.get(holder.getAdapterPosition()).getId(), list.get(holder.getAdapterPosition()).getMealType(), list.get(holder.getAdapterPosition()).getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class FoodAdapterViewHolder extends RecyclerView.ViewHolder {
    TextView textView_list_diary;
    CardView cardView_list_diary;
    public FoodAdapterViewHolder(@NonNull View itemView) {
        super(itemView);

        textView_list_diary = itemView.findViewById(R.id.textView_list_diary);
        cardView_list_diary = itemView.findViewById(R.id.cardView_list_diary);

    }
}
