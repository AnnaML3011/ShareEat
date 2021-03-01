package com.example.shareeat.activities;
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
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.fragments.Fragment_Recent_Recipes;
import com.example.shareeat.fragments.Fragment_wishList;
import com.example.shareeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;


public class Activity_MyFeed extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String F_WHICH_ACTIVITY = "F_WHICH_ACTIVITY";
    private static final String ACTIVITY_MYFEED = "Activity_MyFeed";
    private static final String A_TAG = "A_tag";
    private static final int REQUEST_CODE = 1;
    private AppManager appManager;
    private Button upload_recipe_BTN;
    private ImageButton logout_button;
    private ImageView user_img_IMG;
    private Intent myIntent;
    private Uri imageUri;
    private Uri downloadUri;
    private String uri_string;
    private String userName;
    private String userEmail;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;
    private ImageView user_img_IMG_drawer;
    private TextView user_name_LBL_drawer;
    private TextView user_mail_LBL_drawer;
    private TextView user_name_LBL;
    private Fragment_wishList fragment_wishList;
    private Fragment_Recent_Recipes recent_recipes;
    private String tag;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        appManager = new AppManager(this);
        appManager.findViewsMyFeed(this);
        mAuth = FirebaseAuth.getInstance();
        fragment_wishList = new Fragment_wishList();
        recent_recipes = new Fragment_Recent_Recipes();
        menuNavigation();
        findViews();
        this.setTitle(" ");
        tag = getIntent().getStringExtra(A_TAG);
        if(tag!= null) {
            if (tag.equals("Activity_SignUp")) {
                userEmail = getIntent().getStringExtra("email");
                userName = getIntent().getStringExtra("userName");
                setUserNameAndEmail(userName, userEmail);
            }
        }
        getSupportFragmentManager().beginTransaction().add(R.id.wishlist_LAY_list, fragment_wishList).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.recentRecipes_LAY_list, recent_recipes).commit();
        initViews();

    }

    // Setting menu navigation
    private void menuNavigation() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_LAY);
        nav_view = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        nav_view.setNavigationItemSelectedListener(this);
        toggle.syncState();
    }

    private void findViews(){
        upload_recipe_BTN = appManager.getUpload_recipe_BTN();
        logout_button = appManager.getLogout_button();
        user_img_IMG = appManager.getUser_img_IMG();
        View header_view = nav_view.getHeaderView(0);
        user_img_IMG_drawer = header_view.findViewById(R.id.user_img_IMG_drawer);
        user_name_LBL_drawer = header_view.findViewById(R.id.user_name_LBL_drawer);
        user_mail_LBL_drawer = header_view.findViewById(R.id.user_mail_LBL_drawer);
        user_name_LBL = findViewById(R.id.user_name_LBL);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.upload_recipe_BTN:
                myIntent = new Intent(Activity_MyFeed.this, Activity_UploadReciepe.class);
                Log.d("AAAAAAAAAAA",""+Activity_MyFeed.this.toString());
                myIntent.putExtra(F_WHICH_ACTIVITY, ACTIVITY_MYFEED);
                myIntent.putExtra("userName",userName);
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
            case R.id.user_img_IMG_drawer:
                finish();
                startActivity(getIntent());
                break;
        }
    }
    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            }
            else {
                drawer.openDrawer(Gravity.RIGHT);
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        }
    }

    private void initViews(){
        uri_string = "";
        userName = "";
        userEmail = "";
        upload_recipe_BTN.setOnClickListener(this);
        logout_button.setOnClickListener(this);
        user_img_IMG.setOnClickListener(this);
        user_img_IMG_drawer.setOnClickListener(this);
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

                            Log.d("userDetails" , ""+userName +";;;;;" + userEmail);

                            if(uri_string!= null) {
                                imageUri = Uri.parse(uri_string);
                                changeUserProfileImage();
                            }
                        } else {
                            Log.d("not found", "No such document");
                        }
                        userName = document.getData().get("userName").toString();
                        userEmail = document.getData().get("email").toString();
                        setUserNameAndEmail(userName, userEmail);
                    } else {
                        Log.d("failed", "get failed with ", task.getException());
                    }
                }
            });
        }

    }

    private void setUserNameAndEmail(String name, String email){
        user_name_LBL_drawer.setText(name);
        user_mail_LBL_drawer.setText(email);
        user_name_LBL.setText(name+"!");
    }

    private void changeUserProfileImage() {
        Log.d("success", "\nDocumentSnapshot data: " + "----" + imageUri + user_img_IMG +user_img_IMG_drawer);
        Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(user_img_IMG);
        Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(user_img_IMG_drawer);
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
                            Glide.with(this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(user_img_IMG_drawer);
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.Categories:
                intent = new Intent(Activity_MyFeed.this, Activity_Categories.class);
                startActivity(intent);
                finish();
                break;
            case R.id.My_Recipes:
                intent = new Intent(Activity_MyFeed.this, Activity_MyRecipes.class);
                startActivity(intent);
                finish();
                break;
            case R.id.My_Wish_List:
                intent = new Intent(Activity_MyFeed.this, Activity_MyWishList.class);
                startActivity(intent);
                finish();
                break;
            case R.id.Log_Out:
                FirebaseAuth.getInstance().signOut();
                myIntent = new Intent(Activity_MyFeed.this, Activity_Main.class);
                startActivity(myIntent);
                finish();
                break;
        }
        return true;
    }
}
