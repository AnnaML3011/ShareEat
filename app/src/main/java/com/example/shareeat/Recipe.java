package com.example.shareeat;

public class Recipe {
    String recipeName, recipeDescription;
    String preparationTime;
    public static enum RecipeCategory {Desserts, Breakfasts, Salads, Soups, SideDishes, Italic, Meat, Vegetarian}
    RecipeCategory category;
    String recipeImage;

    public Recipe(){
    }

    public Recipe(String recipeName, String recipeDescription, String preparationTime, RecipeCategory category, String recipeImage) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.preparationTime = preparationTime;
        this.category = category;
        this.recipeImage = recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
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
}
