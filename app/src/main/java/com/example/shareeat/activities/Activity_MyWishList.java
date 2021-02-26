
package com.example.shareeat.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_MyRecipes;
import com.example.shareeat.fragments.Fragment_wishList;
import com.example.shareeat.R;

public class Activity_MyWishList extends AppCompatActivity {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYRECIPES = "Activity_MyRecipes";
    private ImageButton backto_myFeed_BTN;
    private Button upload_recipe_BTN_myRcipes;
    private Fragment_MyRecipes fragment_myRecipes;

    private AppManager appManager;
    RecyclerView myRecipes_RECY_LAY ;
    private Intent myIntent;
    Fragment_wishList fragment_wishList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_wish_list_screen);
        super.onCreate(savedInstanceState);
        appManager = new AppManager(this);
        appManager.findViewsMyRecipes(this);
        fragment_wishList = new Fragment_wishList();
        findViews();
        initViews();
        getSupportFragmentManager().beginTransaction().add(R.id.myWishList_LAY_list, fragment_wishList).commit();
    }

    private void findViews() {
        backto_myFeed_BTN = appManager.getBackto_myFeed_BTN();
    }

    private void initViews() {
        backto_myFeed_BTN.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_MyWishList.this, Activity_MyFeed.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

}

