
package com.example.shareeat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_Categories;
import com.example.shareeat.R;

public class Activity_Categories extends AppCompatActivity  {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYRECIPES = "Activity_MyRecipes";
    private ImageButton backto_myFeed_BTN;
    private Button upload_recipe_BTN_myRcipes;
    private Fragment_Categories fragment_categories;
    private AppManager appManager;
    RecyclerView myRecipes_RECY_LAY ;
    private Intent myIntent;
    private String which_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.categories_screen);
        super.onCreate(savedInstanceState);
        appManager = new AppManager(this);
        appManager.findViewsCategoriesScreen(this);
        fragment_categories = new Fragment_Categories();
        findViews();
        initViews();
        getSupportFragmentManager().beginTransaction().add(R.id.categories_LAY_list, fragment_categories).commit();
    }

    private void findViews() {
        backto_myFeed_BTN = appManager.getBackto_myFeed_BTN();
//        myRecipes_RECY_LAY = findViewById(R.id.myRecipes_RECY_LAY);
//        upload_recipe_BTN_myRcipes = appManager.getUpload_recipe_BTN_myRcipes();

    }

    private void initViews() {
        backto_myFeed_BTN.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_Categories.this, Activity_MyFeed.class);
                startActivity(myIntent);
                finish();
            }
        });
//        upload_recipe_BTN_myRcipes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myIntent = new Intent(Activity_MyRecipes.this, Activity_UploadReciepe.class);
//                myIntent.putExtra(F_WHICH_ACTIVITY, ACTIVITY_MYRECIPES);
//                startActivity(myIntent);
//                finish();
//            }
//        });
//        myRecipes_RECY_LAY.setOnClickListener(new cl);
    }

//    @Override
//    public void onItemClick(View view, int position) {
//
//    }
//
//    @Override
//    public void onReadMoreClicked(View view, Recipe recipe) {
//
//    }
}
