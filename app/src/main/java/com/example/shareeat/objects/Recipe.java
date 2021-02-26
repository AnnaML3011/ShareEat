package com.example.shareeat.objects;

import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Recipe implements Serializable,Comparable<Recipe> {
    String recipeName;
    String recipeIngredients;
    String recipeDirections;
    String preparationTime;
    public static enum RecipeCategory {Desserts, Breakfasts, Salads, Soups, SideDishes, Italic, Meat, Vegetarian, Asian}
    RecipeCategory category;
    String recipeImage;
    boolean isInWishList;
    long recipeTimeAndDate;

    public Recipe(){
    }

    public Recipe(String recipeName,String recipeIngrediants, String recipeDirections, String preparationTime, RecipeCategory category, String recipeImage, boolean isInWishList, long recipeTimeAndDate) {
        this.recipeName = recipeName;
        this.recipeIngredients = recipeIngrediants;
        this.recipeDirections = recipeDirections;
        this.preparationTime = preparationTime;
        this.category = category;
        this.recipeImage = recipeImage;
        this.isInWishList = isInWishList;
        this.recipeTimeAndDate = recipeTimeAndDate;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getRecipeDirections() {
        return recipeDirections;
    }

    public void setRecipeDirections(String recipeDirections) {
        this.recipeDirections = recipeDirections;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public boolean isInWishList() {
        return isInWishList;
    }

    public void setInWishList(boolean inWishList) {
        isInWishList = inWishList;
    }

    public long getTimeAndDate() {
        return recipeTimeAndDate;
    }

    public void setTimeAndDate(long recipeTimeAndDate) {
        this.recipeTimeAndDate = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Recipe o) {
        Date date11;
        Date date22;
        if (o instanceof Recipe) {
            Recipe r = (Recipe) o;
//            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
//            Date date1 = new Date(this.getTimeAndDate());
//            Date date2 = new Date(o.getTimeAndDate());
//            String sDate1 = formatter.format(date1);
//            String sDate2 = formatter.format(date2);
            Log.d("compare" , ""+this.getTimeAndDate()+"/"+o.getTimeAndDate()+"//");
            return (int) (this.getTimeAndDate() - o.getTimeAndDate());
        } else {
            throw new RuntimeException( "Not comparable object");
        }

    }
    public static Comparator<Recipe> RecipeComperator = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return (int) o1.compareTo(o2);
        }
    };
}
