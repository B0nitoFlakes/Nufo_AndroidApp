package com.example.nufo.Models;

import com.example.nufo.Listeners.FoodItem;

import java.util.ArrayList;

public class Ingredient implements FoodItem {
    public int id;
    public String name;
    public double amount;
    public String unit;
    public ArrayList<Nutrient> nutrients;
    public String localizedName;
    public String image;

    @Override
    public int getType() {
        return 0;
    }
}
