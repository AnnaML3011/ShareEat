package com.example.shareeat;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private EditText recipe_category_LBL;
    private EditText recipe_description_LBL;
    private EditText preparation_Time_LBL;
    private String recipeName;
    private Recipe.RecipeCategory recipeCategory;
    private String recipeDes;
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
        recipe_description_LBL = appManager.getRecipe_description_LBL();
        preparation_Time_LBL = appManager.getPreparation_Time_LBL();
    }

    private void initViews() {
        recipe_upload_IMG.setOnClickListener(this);
        doneUpload_BTN.setOnClickListener(this);
        backto_myFeed_BTN.setOnClickListener(this);
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

//    private void uploadRecipe(){
//        addSpecificRecipe();
//        FirebaseFirestore.getInstance().collection("Users")
//                .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid())).collection("userRecipes").document(Objects.requireNonNull(recipeName + "-"+mAuth.getCurrentUser().getUid()))
//                .set(recipes).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(Activity_UploadReciepe.this,"Recipe has been uploaded successfully!",
//                            Toast.LENGTH_LONG).show();
//                    //TODO add progress bar
//                    Intent myIntent = new Intent(Activity_UploadReciepe.this, Activity_MyFeed.class);
//                    startActivity(myIntent);
//                    finish();
//                }else{
//                    Toast.makeText(Activity_UploadReciepe.this,"Failed to upload recipe! Try again!",
//                            Toast.LENGTH_LONG).show();
//                    Log.d("failed","Failed to upload recipe! Try again");
//                }
//            }
//        });
//      updateRecipesList();
//    }

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
                    //TODO add progress bar
                    which_Activity = getIntent().getStringExtra(F_WHICH_ACTIVITY);
                    Log.d("hereeeeeeeeeee",which_Activity);
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
        updateRecipesList();
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
        recipeName = recipe_Name_LBL.getText().toString();
        recipeDes = recipe_description_LBL.getText().toString();
        recipePreTime = preparation_Time_LBL.getText().toString();
        String category = recipe_category_LBL.getText().toString();
        recipeCategory = Recipe.RecipeCategory.valueOf(category);
        imageUri = downloadUri;
        uri_string = imageUri.toString();
        //chnaged the Recipe recipe ->to recipe =
        recipe = new Recipe(recipeName, recipeDes, recipePreTime, recipeCategory, uri_string);
        recipes.add(recipe);
        user.addRecipe(recipes);
        userRecipes.put(recipeName+"-"+mAuth.getCurrentUser().getUid(), recipe);
    }



//    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//        @Override
//        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    document.getData().get("userRecipes");
//                    Log.d("success", "Upload Recipe: " + document.getData().get("userRecipes"));
//                } else {
//                    Log.d("not found", "No such document");
//                }
//            } else {
//                Log.d("failed", "get failed with ", task.getException());
//            }
//        }
//    });
}
