package com.example.nufo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.Listeners.IngredientDetailsListener;
import com.example.nufo.Listeners.OnDishSelectedListener;
import com.example.nufo.Models.IngredientDetailsResponse;
import com.example.nufo.Models.Nutrient;
import com.example.nufo.Models.Result;
import com.example.nufo.R;
import com.example.nufo.RequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DetectNamesAdapter extends RecyclerView.Adapter<DetectNamesAdapter.DetectNamesViewHolder>{

    Context context;
    List<Result> list;
    RequestManager manager;
    Map<String, AtomicInteger> detectedClassCounts;
    private List<DiaryHelperClass> selectedDishes = new ArrayList<>();

    double caloriesValue = 0.0;
    double carbohydratesValue = 0.0;
    double fatsValue = 0.0;
    double proteinValue = 0.0;

    public DetectNamesAdapter(Context context, List<Result> list, RequestManager manager, Map<String, AtomicInteger> detectedClassCounts) {
        this.context = context;
        this.list = list;
        this.manager = manager;
        this.detectedClassCounts = detectedClassCounts;

    }

    @NonNull
    @Override
    public DetectNamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetectNamesViewHolder(LayoutInflater.from(context).inflate(R.layout.detect_food_with_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetectNamesViewHolder holder, int position) {

        Result result = list.get(position);
        String className = result.name;
        int count = detectedClassCounts.get(className).get();

        if(count > 1)
        {
            holder.textView_detectName.setText(result.name + " x " + count);
        }
        else
        {
            holder.textView_detectName.setText(result.name);
        }

        holder.linearLayout_detectedFood.setVisibility(View.GONE);
        holder.random_list_container_detectFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (holder.linearLayout_detectedFood.getVisibility() == View.GONE) {
                    holder.linearLayout_detectedFood.setVisibility(View.VISIBLE);
                    fetchNutritionDetails(holder, list.get(clickedPosition).id, count);

                } else {
                    holder.linearLayout_detectedFood.setVisibility(View.GONE);
                }
            }
        });

        fetchNutritionDetails(holder, result.id, count);

        holder.checkbox_selectToLog.setOnCheckedChangeListener(null); // Reset listener to prevent unwanted calls
        holder.checkbox_selectToLog.setChecked(selectedDishes.stream().anyMatch(dish -> dish.getFoodName().equals(result.name)));

        holder.checkbox_selectToLog.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String calText = holder.textView_detectCal.getText().toString();
                String carbText = holder.textView_detectCarb.getText().toString();
                String fatText = holder.textView_detectFat.getText().toString();
                String proteinText = holder.textView_detectProtein.getText().toString();

                double calories = extractNumericValue(calText);
                double carbs = extractNumericValue(carbText);
                double fats = extractNumericValue(fatText);
                double protein = extractNumericValue(proteinText);

                DiaryHelperClass diaryHelperClass = new DiaryHelperClass(String.valueOf(count), result.name, calories, carbs, fats, protein);
                selectedDishes.add(diaryHelperClass);
                Log.d("DishSelection", "Selected dish: " + result.name);
            } else {
                selectedDishes.removeIf(diaryHelperClass -> diaryHelperClass.getFoodName().equals(result.name));
                Log.d("DishSelection", "Deselected dish: " + result.name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<DiaryHelperClass> getSelectedDishes() {
        return selectedDishes;
    }

    private double extractNumericValue(String text) {
        text = text.replaceAll("[^\\d.]", ""); // Remove any non-numeric characters except dot (.)
        if (text.isEmpty()) {
            return 0.0; // Handle case where there's no numeric value extracted
        }
        return Double.parseDouble(text); // Parse the extracted numeric value to double
    }

    public static class DetectNamesViewHolder extends RecyclerView.ViewHolder{
        TextView textView_detectName, textView_detectId, textView_detectCal, textView_detectCarb, textView_detectProtein, textView_detectFat;
        CardView random_list_container_detectFood;
        LinearLayout linearLayout_detectedFood;
        CheckBox checkbox_selectToLog;

        public DetectNamesViewHolder(@NonNull View itemView) {
            super(itemView);

            random_list_container_detectFood = itemView.findViewById(R.id.random_list_container_detectFood);
            textView_detectName = itemView.findViewById(R.id.textView_detectName);
            textView_detectCal = itemView.findViewById(R.id.textView_detectCal);
            textView_detectCarb = itemView.findViewById(R.id.textView_detectCarb);
            textView_detectProtein = itemView.findViewById(R.id.textView_detectProtein);
            textView_detectFat = itemView.findViewById(R.id.textView_detectFat);
            linearLayout_detectedFood = itemView.findViewById(R.id.linearLayout_detectedFood);
            checkbox_selectToLog = itemView.findViewById(R.id.checkbox_selectToLog);

        }
    }

    private void fetchNutritionDetails(DetectNamesViewHolder holder, int foodId, int count)
    {
        manager.getIngredientDetails(new IngredientDetailsListener() {
            @Override
            public void didFetch(IngredientDetailsResponse response, String message) {
                if (response.nutrition != null) {
                    Log.d("FoodRecognitionActivity", "Nutrition data received");
                    for (Nutrient item : response.nutrition.nutrients) {
                        switch (item.name) {
                            case "Calories":
                                caloriesValue = item.amount;
                                holder.textView_detectCal.setText(item.amount * count+ " " + item.unit);
                                break;
                            case "Carbohydrates":
                                carbohydratesValue = item.amount;
                                holder.textView_detectCarb.setText(item.amount * count + " " + item.unit);
                                break;
                            case "Fat":
                                fatsValue = item.amount;
                                holder.textView_detectFat.setText(item.amount * count+ " " + item.unit);
                                break;
                            case "Protein":
                                proteinValue = item.amount;
                                holder.textView_detectProtein.setText(item.amount * count+ " " + item.unit);
                                break;
                        }
                    }
                    Log.d("FoodRecognitionActivity", "Count : "+ count + " Calories : " + caloriesValue + " Carbs : " + carbohydratesValue + " Protein : " + proteinValue + " Fats : " + fatsValue);
                }
            }

            @Override
            public void didError(String message) {
                Log.e("FoodDetailsActivity", "Error fetching nutrition data: " + message);
            }
        }, foodId);
    }

}
