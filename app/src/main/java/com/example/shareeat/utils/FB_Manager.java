package com.example.shareeat.utils;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class FB_Manager {
    private List<Recipe> recipes_WishList = new ArrayList<>();
    public Recipe.RecipeCategory recipeCategory;
    public Recipe recipe;
    boolean isInWL;


    public void uploadRecipeToUserWishList(Recipe recipe, FirebaseAuth mAuth){
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userWishList").document(Objects.requireNonNull(recipe.getRecipeName() +"-"+recipe.getUserUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Sucsses" , "" +"Recipe has been uploaded successfully to Wishlist!");
                }else{
                    Log.d("failed","Failed to upload recipe to Wishlist! Try again");
                }
            }
        });
    }


    public void addSpecificRecipe(String recipeName, String recipeIng, String recipeDir, String recipePreTime, String category, String imageUri, boolean isInWishList, FirebaseAuth mAuth, String userId, Date date ){
        recipeCategory = Recipe.RecipeCategory.valueOf(category);
        recipe = new Recipe(recipeName, recipeIng, recipeDir, recipePreTime, recipeCategory, imageUri, isInWishList, date, userId);
        recipes_WishList.add(recipe);
        uploadRecipeToUserWishList(recipe, mAuth);
    }

    public void removeRecipeFromWishList(Recipe recipe, FirebaseAuth mAuth , Context context){
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userWishList").document(Objects.requireNonNull(recipe.getRecipeName() +"-"+recipe.getUserUid()))
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Recipe has deleted successfully from Wishlist!",
                            Toast.LENGTH_LONG).show();
                    updateUserRecipesAfterRemoveFromWL(recipe.getRecipeName(), recipe, mAuth);
                }else{
                    Toast.makeText(context,"Failed to delete recipe from Wishlist! Try again!",
                            Toast.LENGTH_LONG).show();
                    Log.d("failed","Failed to delete recipe from Wishlist! Try again");
                }
            }
        });
    }


    public void updateUserRecipesAfterRemoveFromWL(String recipeName ,Recipe recipe,FirebaseAuth mAuth){
        FirebaseFirestore.getInstance().collection("Recipes").document(Objects.requireNonNull(recipeName +"-"+recipe.getUserUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Sucsses" , "" +"Recipe has been uploaded successfully to userRecipes!");
                }else{
                    Log.d("failed","Failed to upload recipe to userRecipes! Try again");
                }
            }
        });
    }


    public void uploadRecipeToUserRecipes(String recipeName ,Recipe recipe,FirebaseAuth mAuth){
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").document(Objects.requireNonNull(recipeName +"-"+mAuth.getCurrentUser().getUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Sucsses" , "" +"Recipe has been uploaded successfully to userRecipes!");
                }else{
                    Log.d("failed","Failed to upload recipe to userRecipes! Try again");
                }
            }
        });
    }

    public void setOnAddToWishList(View view, Recipe recipe, FirebaseAuth mAuth, Context context){
        ImageView save_to_WL_BTN_myRecipes = view.findViewById(R.id.save_to_WL_BTN_myRecipes);
        isInWL = false;
        if(recipe.isInWishList() ==false){
            isInWL = true;
            recipe.setInWishList(isInWL);
            Glide.with(view).load(R.drawable.ic_heart_filled_pink).apply(RequestOptions.circleCropTransform()).into(save_to_WL_BTN_myRecipes);
            addSpecificRecipe(recipe.getRecipeName(), recipe.getRecipeIngredients(),recipe.getRecipeDirections(), recipe.getPreparationTime()
                    ,recipe.getCategory().toString(), recipe.getRecipeImage(), isInWL, mAuth, recipe.getUserUid(), recipe.getRecipeTimeAndDate());
        }else{
            isInWL = false;
            recipe.setInWishList(isInWL);
            Glide.with(view).load(R.drawable.ic_heart_empty).apply(RequestOptions.circleCropTransform()).into(save_to_WL_BTN_myRecipes);
            removeRecipeFromWishList(recipe,mAuth,context);

        }
        updateUserRecipesAfterAddWL(recipe.getRecipeName(), recipe, mAuth);
    }

    public void updateUserRecipesAfterAddWL(String recipeName ,Recipe recipe,FirebaseAuth mAuth){
        FirebaseFirestore.getInstance().collection("Recipes").document(Objects.requireNonNull(recipeName +"-"+recipe.getUserUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("Sucsses" , "" +"Recipe has been uploaded successfully to userRecipes!");
                }else{
                    Log.d("failed","Failed to upload recipe to userRecipes! Try again");
                }
            }
        });
    }
}
