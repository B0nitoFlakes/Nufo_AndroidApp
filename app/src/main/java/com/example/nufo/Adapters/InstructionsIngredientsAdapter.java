package com.example.nufo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Models.Ingredient;
import com.example.nufo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionsIngredientsAdapter extends RecyclerView.Adapter<InstructionIngredientViewHolder>{

    Context context;
    List<Ingredient> list;

    public InstructionsIngredientsAdapter(Context context, List<Ingredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionIngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionIngredientViewHolder holder, int position) {
        holder.textView_instructions_step_item.setText(list.get(position).name);
        holder.textView_instructions_step_item.setSelected(true);
        String imageUrl = list.get(position).image;
//        Picasso.get().load(list.get(position).image).into(holder.imageView_instructions_step_item);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(list.get(position).image).into(holder.imageView_instructions_step_item);
            // Print the image URL
            System.out.println("Image URL: " + imageUrl);
        } else {
            // Load placeholder image from resources
            Picasso.get().load(R.drawable.noimage).into(holder.imageView_instructions_step_item);
            System.out.println("Image URL is null or empty for position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionIngredientViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView_instructions_step_item;
    TextView textView_instructions_step_item;
    public InstructionIngredientViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView_instructions_step_item = itemView.findViewById(R.id.imageView_instructions_step_item);
        textView_instructions_step_item = itemView.findViewById(R.id.textView_instructions_step_item);

    }
}

