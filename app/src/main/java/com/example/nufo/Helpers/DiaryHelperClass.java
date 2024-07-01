package com.example.nufo.Helpers;

public class DiaryHelperClass {
    String foodName, id, mealType, date, amount;
    double caloriesValue, carbohydratesValue, proteinValue, fatsValue;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCaloriesValue() {
        return caloriesValue;
    }

    public void setCaloriesValue(double caloriesValue) {
        this.caloriesValue = caloriesValue;
    }

    public double getCarbohydratesValue() {
        return carbohydratesValue;
    }

    public void setCarbohydratesValue(double carbohydratesValue) {
        this.carbohydratesValue = carbohydratesValue;
    }

    public double getProteinValue() {
        return proteinValue;
    }

    public void setProteinValue(double proteinValue) {
        this.proteinValue = proteinValue;
    }

    public double getFatsValue() {
        return fatsValue;
    }

    public void setFatsValue(double fatsValue) {
        this.fatsValue = fatsValue;
    }

    public DiaryHelperClass(String amount, String foodName, double caloriesValue, double carbohydratesValue, double proteinValue, double fatsValue) {
        this.amount = amount;
        this.foodName = foodName;
        this.caloriesValue = caloriesValue;
        this.carbohydratesValue = carbohydratesValue;
        this.proteinValue = proteinValue;
        this.fatsValue = fatsValue;
    }

    public DiaryHelperClass()
    {

    }

}
