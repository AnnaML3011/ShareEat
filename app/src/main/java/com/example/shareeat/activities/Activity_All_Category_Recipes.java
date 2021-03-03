package com.example.shareeat.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shareeat.fragments.Fragment_All_Category_Recipes;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.R;

public class Activity_All_Category_Recipes extends AppCompatActivity {
    private static final String CATEGORY = "category";
    private ImageButton backto_myFeed_BTN;
    private AppManager appManager;
    private String category;
    private Fragment_All_Category_Recipes fragment_all_category_recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.all_category_recipes_screen);
        super.onCreate(savedInstanceState);
        appManager = new AppManager(this);
        appManager.findViewsCategoriesScreen(this);
        fragment_all_category_recipes = new Fragment_All_Category_Recipes();
        findViews();
        initViews();
        category = getIntent().getStringExtra(CATEGORY);
        getSupportFragmentManager().beginTransaction().add(R.id.categories_LAY_list, fragment_all_category_recipes).commit();
        fragment_all_category_recipes.refresh(category);
    }

    private void findViews() {
        backto_myFeed_BTN = appManager.getBackto_myFeed_BTN();
    }

    private void initViews() {
        backto_myFeed_BTN.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_All_Category_Recipes.this, Activity_Categories.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
