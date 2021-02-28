package com.example.shareeat.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_MyRecipes;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Activity_Specific_Recipe extends AppCompatActivity {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYRECIPES = "Activity_MyRecipes";
    private ImageButton back_button;
    private Button done_With_Recipe_BTN;

    private Fragment_MyRecipes fragment_myRecipes;
//    private Fragment_Recipe fragment_recipe;

    private AppManager appManager;
    RecyclerView myRecipes_RECY_LAY ;
    private TextView recipe_title_LBL;
    private TextView recipe_ingredients_LBL;
    private TextView recipe_directions_LBL;
    private TextView recipe_prep_time;
    private TextView recipe_category;
    private ImageView recipe_scpecific_IMG;
    private Recipe recipe = new Recipe();
    private String fragment_tag;
    private Intent myIntent;
    private ImageView edit_BTN;

    //TODO CHECK WHY BUTTON GARY AND + NOT IN CENTER


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.specific_recipe_layout);
        super.onCreate(savedInstanceState);
        appManager = new AppManager(this);
        appManager.findViewsMyRecipes(this);
        fragment_myRecipes = new Fragment_MyRecipes();
//        fragment_recipe = new Fragment_Recipe();
        findViews();
        initViews();
//        getSupportFragmentManager().beginTransaction().add(R.id.myRecipes_LAY_list, fragment_myRecipes).commit();
    }

    private void findViews() {
        back_button = findViewById(R.id.back_button);
        done_With_Recipe_BTN = findViewById(R.id.done_With_Recipe_BTN);
//        myRecipes_RECY_LAY = findViewById(R.id.myRecipes_RECY_LAY);
        recipe_title_LBL = findViewById(R.id.recipe_title_LBL);
        recipe_ingredients_LBL = findViewById(R.id.recipe_ingredients_LBL);
        recipe_directions_LBL = findViewById(R.id.recipe_directions_LBL);
        recipe_prep_time = findViewById(R.id.recipe_prep_time);
        recipe_category = findViewById(R.id.recipe_category);
        recipe_scpecific_IMG = findViewById(R.id.recipe_scpecific_IMG);
        edit_BTN = findViewById(R.id.edit_BTN);
    }

    private void initViews() {
        recipe = (Recipe) getIntent().getSerializableExtra("Recipe");
        fragment_tag = getIntent().getStringExtra("tag");
//        Log.d("Dddd",""+recipe.getRecipeName());
        set_all_recipe_info(recipe);
        back_button.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWhichActivityToGo();
            }
        });
        done_With_Recipe_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWhichActivityToGo();
            }
        });
        edit_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myIntent = new Intent(Activity_Specific_Recipe.this, Activity_Edit_Recipe.class);
                myIntent.putExtra("isInWL", recipe.isInWishList());
                myIntent.putExtra("Recipe",recipe);
                myIntent.putExtra("tag",fragment_tag);
                startActivity(myIntent);
                finish();
            }
        }) ;
    }

    public void checkWhichActivityToGo(){
        //TODO CHANGE TO SWITCH CASE
        switch (fragment_tag) {
            case "Fragment_MyRecipes":
                myIntent = new Intent(Activity_Specific_Recipe.this, Activity_MyRecipes.class);
                myIntent.putExtra("Recipe",recipe);
                startActivity(myIntent);
                finish();
                break;
            case "Fragment_Categories":
                myIntent = new Intent(Activity_Specific_Recipe.this, Activity_Categories.class);
                myIntent.putExtra("Recipe",recipe);
                startActivity(myIntent);
                finish();
                break;
            case "Fragment_wishList":
            case "Fragment_Recent_Recipes":
                myIntent = new Intent(Activity_Specific_Recipe.this, Activity_MyFeed.class);
                myIntent.putExtra("Recipe",recipe);
                startActivity(myIntent);
                finish();
                break;
            case "Fragment_myWL":
                myIntent = new Intent(Activity_Specific_Recipe.this, Activity_MyWishList.class);
                myIntent.putExtra("Recipe",recipe);
                startActivity(myIntent);
                finish();
                break;
        }
    }

    public void set_all_recipe_info(Recipe recipe){
        recipe_title_LBL.setText(recipe.getRecipeName());
        recipe_ingredients_LBL.setText(recipe.getRecipeIngredients());
        recipe_directions_LBL.setText(recipe.getRecipeDirections());
        recipe_prep_time.setText(recipe.getPreparationTime());
        recipe_category.setText(recipe.getCategory().toString());
        Glide.with(this).load(recipe.getRecipeImage()).apply(RequestOptions.centerInsideTransform()).into(recipe_scpecific_IMG);
    }
}

