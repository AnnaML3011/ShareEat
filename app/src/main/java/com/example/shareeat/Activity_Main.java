package com.example.shareeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.annotation.Documented;

public class Activity_Main extends AppCompatActivity implements View.OnClickListener {
    private AppManager appManager;
    private Button login_BTN;
    private Button signup_manualy_BTN;
    private Button google_BTN_signup;
    private EditText login_email_LBL;
    private EditText login_password_LBL;
    private String entered_email;
    private String entered_pass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        appManager = new AppManager(this);
        appManager.findViewsLogin(this);
        mAuth = FirebaseAuth.getInstance();
        initViews();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_BTN:
                loginUser();
//                startActivity(new Intent(Activity_Main.this, Activity_MyFeed.class));
                break;
            case R.id.signup_manualy_BTN:
                startActivity(new Intent(Activity_Main.this,Activity_SignUp.class));
                break;
            case R.id.google_BTN_signup:
                startActivity(new Intent(Activity_Main.this, Activity_SignUp.class));
                break;
        }
    }

    private void initViews(){
        login_BTN = appManager.getLogin_BTN();
        signup_manualy_BTN = appManager.getSignup_manualy_BTN();
        google_BTN_signup = appManager.getGoogle_BTN_signup();
        login_email_LBL = appManager.getLogin_email_LBL();
        login_password_LBL= appManager.getLogin_password_LBL();
        login_BTN.setOnClickListener(this);
        signup_manualy_BTN.setOnClickListener(this);
        google_BTN_signup.setOnClickListener(this);
    }

    private void loginUser() {
        entered_email = login_email_LBL.getText().toString();
        entered_pass = login_password_LBL.getText().toString();
        if (entered_email.isEmpty() | entered_pass.isEmpty()) {
        Toast.makeText(Activity_Main.this,"Email or password is empty, please fill in the empty fields!",
                Toast.LENGTH_LONG).show();
            return;
        } else {
            isUserExist();
        }
    }

    private void isUserExist() {
        final String userEnteredEmail = login_email_LBL.getText().toString().trim();
        final String userEnteredPassword = login_password_LBL.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If user exist , we read his data from DB
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), Activity_MyFeed.class);
                            startActivity(intent);
//                            readUserFromDB(user.getUid());
                       } else {
                            // If the system is unable to find the user, it issues an error message accordingly
                            if (task.getException().getMessage().contains("email")) {
                                login_email_LBL.setError(task.getException().getMessage());
                                Toast.makeText(Activity_Main.this,"Cant find user with email:" + login_email_LBL.getText().toString()+"!",
                                        Toast.LENGTH_LONG).show();
                            } else if (task.getException().getMessage().contains("password")) {
                                login_password_LBL.setError(task.getException().getMessage());
                                Toast.makeText(Activity_Main.this,"Incorrect password!",
                                        Toast.LENGTH_LONG).show();
                            } else if (task.getException().getMessage().contains("No such user!")) {
                                login_email_LBL.setError(task.getException().getMessage());
                                Toast.makeText(Activity_Main.this,"No such user!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    // Read user data from DB
//    void readUserFromDB(final String userID) {
//        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users/").document(userID);
//
//        reference.addSnapshotListener(new EventListener() {
//            @Override
//            public void onEvent(@Nullable Object value, @Nullable FirebaseFirestoreException error) {
//
//            }
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                Intent intent = new Intent(getApplicationContext(), Activity_MyFeed.class);
//                intent.putExtra("userInfo", String.valueOf(user));
//                startActivity(intent);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


//
//    private void readUserRecipesFromDB(final String userID) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Confectioneries/").child(userID);
//
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Shop shop = snapshot.getValue(Shop.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}