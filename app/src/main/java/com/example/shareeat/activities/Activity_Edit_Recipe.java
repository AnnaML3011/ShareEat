package com.example.shareeat.activities;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_MyRecipes;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.utils.FB_Manager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class Activity_Edit_Recipe extends AppCompatActivity {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYRECIPES = "Activity_MyRecipes";
    private static final int REQUEST_CODE = 1;
    private ImageButton back_button;
    private Button done_With_Edit_Recipe_BTN;

    private Fragment_MyRecipes fragment_myRecipes;
//    private Fragment_Recipe fragment_recipe;

    private AppManager appManager;
    RecyclerView myRecipes_RECY_LAY ;
    private TextView recipe_title_LBL;
    private EditText recipe_ingredients_LBL;
    private EditText recipe_directions_LBL;
    private EditText recipe_prep_time;
    private Spinner recipe_category;
    private ImageView recipe_scpecific_Edit_IMG;
    private Recipe recipe = new Recipe();
    private boolean isRecipeInwl;
    private Intent myIntent;
    private String recipeName;
    private Recipe.RecipeCategory recipeCategory;
    private String recipeIng;
    private String recipeDir;
    private String recipePreTime;
    private Uri imageUri;
    private Uri downloadUri;
    private String uri_string;
    private StorageReference storageReference;
    private  FirebaseAuth mAuth;
    private String fragment_tag;
    private FB_Manager fb_manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.specific_recipe_edit_layout);
        super.onCreate(savedInstanceState);
        appManager = new AppManager(this);
        appManager.findViewsMyRecipes(this);
        fb_manager = new FB_Manager();
        fragment_myRecipes = new Fragment_MyRecipes();
        mAuth = FirebaseAuth.getInstance();

//        fragment_recipe = new Fragment_Recipe();
        findViews();
        initViews();
//        getSupportFragmentManager().beginTransaction().add(R.id.myRecipes_LAY_list, fragment_myRecipes).commit();
    }

    private void findViews() {
        back_button = findViewById(R.id.back_button);
        done_With_Edit_Recipe_BTN = findViewById(R.id.done_With_Edit_Recipe_BTN);
        recipe_ingredients_LBL = findViewById(R.id.recipe_ingredients_LBL);
        recipe_directions_LBL = findViewById(R.id.recipe_directions_LBL);
        recipe_prep_time = findViewById(R.id.recipe_prep_time);
        recipe_category = findViewById(R.id.recipe_category);
        recipe_scpecific_Edit_IMG = findViewById(R.id.recipe_scpecific_Edit_IMG);
        recipe_title_LBL =findViewById(R.id.recipe_title_LBL);
    }

    private void initViews() {
        fragment_tag = getIntent().getStringExtra("tag");
        isRecipeInwl = getIntent().getBooleanExtra("isInWL",false);
        recipe = (Recipe) getIntent().getSerializableExtra("Recipe");
        set_all_recipe_info(recipe);
//        Log.d("Dddd",""+recipe.getRecipeName());
        back_button.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myIntent = new Intent(Activity_Edit_Recipe.this, Activity_Specific_Recipe.class);
                myIntent.putExtra("Recipe",recipe);
                myIntent.putExtra("tag",fragment_tag);
                startActivity(myIntent);
                finish();
            }
        });
        done_With_Edit_Recipe_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("done","");
//                uploadRecipe();
                uploadImageToDB();
            }
        });
        recipe_scpecific_Edit_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    public void set_all_recipe_info(Recipe recipe){
        recipe_title_LBL.setText(recipe.getRecipeName());
        recipe_ingredients_LBL.setText(recipe.getRecipeIngredients());
        recipe_directions_LBL.setText(recipe.getRecipeDirections());
        recipe_prep_time.setText(recipe.getPreparationTime());
        Glide.with(this).load(recipe.getRecipeImage()).apply(RequestOptions.centerInsideTransform()).into(recipe_scpecific_Edit_IMG);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_CODE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        imageUri = data.getData();
                        if(imageUri!= null){
                            Glide.with(this).load(imageUri).apply(RequestOptions.centerCropTransform()).into(recipe_scpecific_Edit_IMG);
                        }
                        //data gives you the image uri. Try to convert that to bitmap
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.d("failed", "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("Exception", "Exception in onActivityResult : " + e.getMessage());
        }
    }

    private void uploadImageToDB() {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (imageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference("recipesImages").child(System.currentTimeMillis()
                    + "." + mime.getExtensionFromMimeType(cR.getType(imageUri)));

            storageReference.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    uploadRecipe();
                    fb_manager.uploadRecipeToUserWishList(recipe, mAuth);
                }
            });
        }else{
            Toast.makeText(Activity_Edit_Recipe.this,"Please upload an Recipe image!",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void getAllRecipeInfo(){
        String category = "";
        recipeName = recipe.getRecipeName();
        recipeIng = recipe_ingredients_LBL.getText().toString();
        recipeDir = recipe_directions_LBL.getText().toString();
        recipePreTime = recipe_prep_time.getText().toString();
        if(!recipe_category.getSelectedItem().toString().equals("Category")) {
           category = recipe_category.getSelectedItem().toString();
           Log.d("aaaaaaaaaaaaaaa",""+category);
           recipeCategory = Recipe.RecipeCategory.valueOf(category);
        }else{
            recipeCategory = recipe.getCategory();
        }
        imageUri = downloadUri;
        if(imageUri != null){
            uri_string = imageUri.toString();
        }else {
            uri_string = recipe.getRecipeImage();
        }
        recipe = new Recipe(recipeName, recipeIng, recipeDir, recipePreTime, recipeCategory, uri_string ,isRecipeInwl, recipe.getTimeAndDate());
    }

    private void uploadRecipe(){
        getAllRecipeInfo();
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").document(Objects.requireNonNull(recipeName +"-"+mAuth.getCurrentUser().getUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Activity_Edit_Recipe.this,"Recipe has been uploaded successfully!",
                            Toast.LENGTH_LONG).show();
                    //TODO add progress bar
                    myIntent = new Intent(Activity_Edit_Recipe.this, Activity_Specific_Recipe.class);
                    myIntent.putExtra("Recipe",recipe);
                    myIntent.putExtra("tag",fragment_tag);
                    startActivity(myIntent);
                    finish();
                }else{
                    Toast.makeText(Activity_Edit_Recipe.this,"Failed to upload recipe! Try again!",
                            Toast.LENGTH_LONG).show();
                    Log.d("failed","Failed to upload recipe! Try again");
                }
            }
        });
//        updateRecipesList();
    }


//    public void checkWhichActivityToGo(){
//        //TODO CHANGE TO SWITCH CASE
//        switch (fragment_tag) {
//            case "Fragment_MyRecipes":
//                myIntent = new Intent(Activity_Edit_Recipe.this, Activity_MyRecipes.class);
//                startActivity(myIntent);
//                finish();
//                break;
//            case "Fragment_Categories":
//                myIntent = new Intent(Activity_Edit_Recipe.this, Activity_Categories.class);
//                startActivity(myIntent);
//                finish();
//                break;
//            case "Fragment_wishList":
//            case "Fragment_Recent_Recipes":
//                myIntent = new Intent(Activity_Edit_Recipe.this, Activity_MyFeed.class);
//                startActivity(myIntent);
//                finish();
//                break;
//            case "Fragment_myWL":
//                myIntent = new Intent(Activity_Edit_Recipe.this, Activity_MyWishList.class);
//                startActivity(myIntent);
//                finish();
//                break;
//        }
//    }

//    public void set_all_recipe_info(Recipe recipe){
//        recipe_title_LBL.setText(recipe.getRecipeName());
//        recipe_ingredients_LBL.setText(recipe.getRecipeIngredients());
//        recipe_directions_LBL.setText(recipe.getRecipeDirections());
//        recipe_prep_time.setText(recipe.getPreparationTime());
//        recipe_category.setText(recipe.getCategory().toString());
//        Glide.with(this).load(recipe.getRecipeImage()).apply(RequestOptions.centerInsideTransform()).into(recipe_scpecific_IMG);
//    }
}

