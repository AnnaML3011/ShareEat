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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shareeat.utils.AppManager;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Activity_UploadReciepe extends AppCompatActivity implements View.OnClickListener {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final int REQUEST_CODE = 1;
    private AppManager appManager;
    //layouts
    private Button doneUpload_BTN;
    private ImageButton backto_myFeed_BTN;
    private ImageView recipe_upload_IMG;
    private EditText recipe_Name_LBL;
    private Spinner recipe_category_LBL;
    EditText recipe_ingredients_UPLD_LBL;
    EditText recipe_directions_UPLD_LBL;
    private EditText preparation_Time_LBL;
    private String recipeName;
    private Recipe.RecipeCategory recipeCategory;
    private String recipeIng;
    private String recipeDir;
    private String recipePreTime;
    private FirebaseAuth mAuth;
    private Recipe recipe;
    private User user;
    private ArrayList<Recipe> recipes;
    private Map<String, Object> userRecipes;
    private Uri imageUri;
    private Uri downloadUri;
    private String uri_string;
    private StorageReference storageReference;
    private String which_Activity;
    private boolean isInWL;
    private String userName;
    private String userEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_reciepe_screen);
        appManager = new AppManager(this);
        appManager.findViewsUploadReciepe(this);
        mAuth = FirebaseAuth.getInstance();
        user = new User();
        recipes = new ArrayList<>();
        userRecipes = new HashMap<>();
        findViews();
        initViews();
    }

    private void findViews() {
        doneUpload_BTN = appManager.getDoneUpload_BTN();
        backto_myFeed_BTN = appManager.getBackto_myFeed_BTN();
        recipe_upload_IMG = appManager.getRecipe_upload_IMG();
        recipe_Name_LBL = appManager.getRecipe_Name_LBL();
        recipe_category_LBL = appManager.getRecipe_category_LBL();
        recipe_ingredients_UPLD_LBL = appManager.getRecipe_ingredients_UPLD_LBL();
        recipe_directions_UPLD_LBL = appManager.getRecipe_directions_UPLD_LBL();
        preparation_Time_LBL = appManager.getPreparation_Time_LBL();
    }

    private void initViews() {
        recipe_upload_IMG.setOnClickListener(this);
        doneUpload_BTN.setOnClickListener(this);
        backto_myFeed_BTN.setOnClickListener(this);
        userEmail = getIntent().getStringExtra("email");
        userName = getIntent().getStringExtra("userName");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.recipe_upload_IMG:
                chooseImage();
                break;
            case R.id.doneUpload_BTN:
                uploadImageToDB();
                break;
            case R.id.backto_myFeed_BTN:
                Intent myIntent = new Intent(Activity_UploadReciepe.this, Activity_MyFeed.class);
                startActivity(myIntent);
                finish();
                break;
        }
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
                            Glide.with(this).load(imageUri).apply(RequestOptions.centerCropTransform()).into(recipe_upload_IMG);
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
                }
            });
        }else{
            Toast.makeText(Activity_UploadReciepe.this,"Please upload an Recipe image!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void uploadRecipe(){
        addSpecificRecipe();
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").document(Objects.requireNonNull(recipeName +"-"+mAuth.getCurrentUser().getUid()))
                .set(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Activity_UploadReciepe.this,"Recipe has been uploaded successfully!",
                            Toast.LENGTH_LONG).show();
                    updateRecipesList();
                    //TODO add progress bar
                    which_Activity = getIntent().getStringExtra(F_WHICH_ACTIVITY);
                    if(which_Activity.equals("Activity_MyRecipes")) {
                        Intent myIntent = new Intent(Activity_UploadReciepe.this, Activity_MyRecipes.class);
                        startActivity(myIntent);
                        finish();
                    }
                    else if(which_Activity.equals("Activity_MyFeed")){
                        Intent myIntent = new Intent(Activity_UploadReciepe.this, Activity_MyFeed.class);
                        startActivity(myIntent);
                        finish();
                    }
                }else{
                    Toast.makeText(Activity_UploadReciepe.this,"Failed to upload recipe! Try again!",
                            Toast.LENGTH_LONG).show();
                    Log.d("failed","Failed to upload recipe! Try again");
                }
            }
        });
//        updateRecipesList();
    }

    private void updateRecipesList() {
        FirebaseFirestore.getInstance().collection("Users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).update("userRecipes",userRecipes).addOnSuccessListener(aVoid -> {
            Log.d("SUCCESS", "userRecipes");
        })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSpecificRecipe(){
        isInWL = false;
        recipeName = recipe_Name_LBL.getText().toString();
        recipeIng = recipe_ingredients_UPLD_LBL.getText().toString();
        recipeDir = recipe_directions_UPLD_LBL.getText().toString();
        recipePreTime = preparation_Time_LBL.getText().toString();
        String category = recipe_category_LBL.getSelectedItem().toString();
        recipeCategory = Recipe.RecipeCategory.valueOf(category);
        imageUri = downloadUri;
        uri_string = imageUri.toString();
        recipe = new Recipe(recipeName, recipeIng, recipeDir, recipePreTime, recipeCategory, uri_string ,isInWL, System.currentTimeMillis());
        recipes.add(recipe);
        user.addRecipe(recipes);
        updateUserRecipes();
        userRecipes.put(recipeName+"-"+mAuth.getCurrentUser().getUid(), recipe);
    }

    private void updateUserRecipes(){
        FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("empty", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            for(DocumentSnapshot ds : documentSnapshots.getDocuments())   {
                                Recipe recipe1 = ds.toObject(Recipe.class);
                                userRecipes.put(recipeName+"-"+mAuth.getCurrentUser().getUid(), recipe1);                            }
                        }
                    }
                });

    }



}
