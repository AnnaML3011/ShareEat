package com.example.shareeat.objects;
import java.io.Serializable;
import java.util.Date;

public class Recipe implements Serializable {
    String recipeName;
    String recipeIngredients;
    String recipeDirections;
    String preparationTime;
    public static enum RecipeCategory {Desserts, Breakfasts, Salads, Soups, SideDishes, Italic, Meat, Vegetarian, Asian}
    RecipeCategory category;
    String recipeImage;
    boolean isInWishList;
    Date recipeTimeAndDate;
    String userUid;



    public Recipe(){
    }

    public Recipe(String recipeName, String recipeIngredients, String recipeDirections, String preparationTime, RecipeCategory category, String recipeImage, boolean isInWishList, Date recipeTimeAndDate, String userUid) {
        this.recipeName = recipeName;
        this.recipeIngredients = recipeIngredients;
        this.recipeDirections = recipeDirections;
        this.preparationTime = preparationTime;
        this.category = category;
        this.recipeImage = recipeImage;
        this.isInWishList = isInWishList;
        this.recipeTimeAndDate = recipeTimeAndDate;
        this.userUid = userUid;
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

    public Date getRecipeTimeAndDate() {
        return recipeTimeAndDate;
    }

    public void setRecipeTimeAndDate(Date recipeTimeAndDate) {
        this.recipeTimeAndDate = recipeTimeAndDate;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
