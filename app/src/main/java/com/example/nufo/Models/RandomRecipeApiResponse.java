package com.example.nufo.Models;

import com.example.nufo.Listeners.FoodItem;

import java.util.ArrayList;

public class RandomRecipeApiResponse implements FoodItem {
    public ArrayList<Recipe> recipes;

    public int getType() {
        return 1; // or any unique identifier for recipes
    }
}
