package com.example.shareeat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

public class Activity_MyFeed extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYFEED = "Activity_MyFeed";
    private static final int REQUEST_CODE = 1;
    private AppManager appManager;
    private Button upload_recipe_BTN;
    private ImageButton logout_button;
    private ImageView user_img_IMG;
    private Intent myIntent;
    private Uri imageUri;
    private Uri downloadUri;
    private String uri_string;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private Object DocumentSnapshot;
    private ImageView drawer_menu_IMG;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;
    private AppBarConfiguration mAppBarConfiguration;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_feed_screen);
        appManager = new AppManager(this);
        appManager.findViewsMyFeed(this);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_LAY);
        nav_view = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);


//        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        Log.d("hereeeeeeeee",""+toggle + "drawer:" +drawer);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//        menuNavigation();

    }
    // Setting menu navigation
    private void menuNavigation() {

//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.Categories, R.id.My_Recipes, R.id.WishList)
//                .setDrawerLayout(drawer)
//                .build();
//        drawer.openDrawer(GravityCompat.START);
//         nav_view.bringToFront();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(nav_view, navController);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        nav_view.setNavigationItemSelectedListener(this);
        toggle.syncState();
    }

    private void findViews(){
        upload_recipe_BTN = appManager.getUpload_recipe_BTN();
        logout_button = appManager.getLogout_button();
        user_img_IMG = appManager.getUser_img_IMG();
        drawer_menu_IMG = appManager.getDrawer_menu_IMG();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.upload_recipe_BTN:
                myIntent = new Intent(Activity_MyFeed.this, Activity_UploadReciepe.class);
                Log.d("AAAAAAAAAAA",""+Activity_MyFeed.this.toString());
                myIntent.putExtra(F_WHICH_ACTIVITY, ACTIVITY_MYFEED);
                startActivity(myIntent);
                finish();
                break;
            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                myIntent = new Intent(Activity_MyFeed.this, Activity_Main.class);
                startActivity(myIntent);
                finish();
                break;
            case R.id.user_img_IMG:
                chooseImage();
                break;
            case R.id.drawer_menu_IMG: //for now just goes to MyRecipes
                //TODO- check why drawer doesn't work.
                myIntent = new Intent(Activity_MyFeed.this, Activity_MyRecipes.class);
                startActivity(myIntent);
                finish();
                break;
        }
    }
//    @Override
//    public void onBackPressed(){
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }else{
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (drawer.isDrawerOpen(Gravity.LEFT)) {
//            drawer.closeDrawer(Gravity.LEFT);
//        }
//    }

    private void initViews(){
        uri_string = "";
        upload_recipe_BTN.setOnClickListener(this);
        logout_button.setOnClickListener(this);
        user_img_IMG.setOnClickListener(this);
        drawer_menu_IMG.setOnClickListener(this);
        if (imageUri == null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid()));
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.getData().get("userImage") != null) {
                                uri_string = document.getData().get("userImage").toString();
                                if(uri_string!= null) {
                                    imageUri = Uri.parse(uri_string);
                                    changeUserProfileImage();
                                    Log.d("success", "DocumentSnapshot data: " + document.getData().get("userImage") + "----" + imageUri);
                                }
                        } else {
                            Log.d("not found", "No such document");
                        }
                    } else {
                        Log.d("failed", "get failed with ", task.getException());
                    }
                }
            });
        }

    }

    private void changeUserProfileImage() {
        Log.d("success", "\nDocumentSnapshot data: " + "----" + imageUri);
        Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(user_img_IMG);
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
                            Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(user_img_IMG);
                            uploadImageToDB();
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
            storageReference = FirebaseStorage.getInstance().getReference("userImages").child(System.currentTimeMillis()
                    + "." + mime.getExtensionFromMimeType(cR.getType(imageUri)));

            storageReference.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser().getUid()));
                    imageUri = downloadUri;
                    documentReference.update("userImage", imageUri.toString());
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
