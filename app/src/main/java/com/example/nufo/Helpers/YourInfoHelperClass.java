package com.example.nufo.Helpers;

public class YourInfoHelperClass {

    public String name, gender, weightGoal;
    public float age, height, weight;
    public float activityLevel;
    public double bmr, goal;

    public YourInfoHelperClass(String name, String gender, String weightGoal, float age, float height, float weight, float activityLevel, double bmr, double goal) {
        this.name = name;
        this.gender = gender;
        this.weightGoal = weightGoal;
        this.age = age;
        this.height = height;
        this.activityLevel = activityLevel;
        this.weight = weight;
        this.bmr = bmr;
        this.goal = goal;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public float getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(float activityLevel) {
        this.activityLevel = activityLevel;  // Add this line
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public YourInfoHelperClass() {
    }
}
