package com.example.shareeat.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_Categories;
import com.example.shareeat.R;


public class Activity_Categories extends AppCompatActivity  {
    private ImageButton backto_myFeed_BTN;
    private Fragment_Categories fragment_categories;
    private AppManager appManager;

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
    }
}
