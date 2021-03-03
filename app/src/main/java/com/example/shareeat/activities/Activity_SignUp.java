package com.example.shareeat.activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;
import com.example.shareeat.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Objects;


public class Activity_SignUp extends AppCompatActivity {
    private static final String A_TAG = "A_tag";
    private static final String EMAIL = "email";
    private static final String UNAME = "userName";
    private ArrayList<Recipe> userRecipes;
    private ArrayList<Recipe> wishList;
    private AppManager appManager;
    private FirebaseAuth mAuth;
    private Button signup_BTN;
    private ImageButton back_button;
    private EditText signUp_uName_LBL;
    private EditText signUp_email_LBL;
    private EditText signUp_password_LBL;
    private EditText verify_password_LBL;
    private String uName;
    private String email;
    private String password;
    private String password_ver;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        appManager = new AppManager(this);
        appManager.findViewsSignUp(this);
        mAuth = FirebaseAuth.getInstance();
        initViews();
    }

    private void initViews(){
        signup_BTN = appManager.getSignup_BTN();
        signUp_uName_LBL = appManager.getSignUp_uName_LBL();
        signUp_email_LBL = appManager.getSignUp_email_LBL();
        signUp_password_LBL = appManager.getSignUp_password_LBL();
        verify_password_LBL = appManager.getVerify_password_LBL();
        back_button = appManager.getBack_button();
        signup_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_SignUp.this, Activity_Main.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    private void signUpUser(){
        uName = signUp_uName_LBL.getText().toString();
        email = signUp_email_LBL.getText().toString();
        password = signUp_password_LBL.getText().toString();
        password_ver = verify_password_LBL.getText().toString();
        if(uName.isEmpty()){
            signUp_uName_LBL.setError("UserName is Required");
            signUp_uName_LBL.requestFocus();
            return;
        }

        if(email.isEmpty()){
            signUp_email_LBL.setError("Email is Required");
            signUp_email_LBL.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUp_email_LBL.setError("Please provide valid email!");
            signUp_email_LBL.requestFocus();
            return;
        }

        if(password.isEmpty()){
            signUp_password_LBL.setError("Password is Required");
            signUp_password_LBL.requestFocus();
            return;
        }

        else if(password.length() < 6){
            signUp_password_LBL.setError("Password is too short, please enter at least 6 chars password");

        }

        if(password_ver.isEmpty()){
            verify_password_LBL.setError("Enter again your password");
            verify_password_LBL.requestFocus();
            return;
        }

        if(!password_ver.equals(password)){
            verify_password_LBL.setError("Password verification not match original password");
            verify_password_LBL.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Activity_SignUp.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userRecipes = new ArrayList<>();
                    wishList = new ArrayList<>();
                    User user = new User(email, uName, userRecipes, wishList,null);
                    FirebaseFirestore.getInstance().collection("Users")
                            .document(Objects.requireNonNull(mAuth.getCurrentUser().getUid()))
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Activity_SignUp.this,"User has been registered successfully!",
                                        Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(Activity_SignUp.this, Activity_MyFeed.class);
                                myIntent.putExtra(A_TAG,"Activity_SignUp");
                                myIntent.putExtra(EMAIL,email);
                                myIntent.putExtra(UNAME,uName);
                                startActivity(myIntent);
                                finish();
                            }else{
                                Toast.makeText(Activity_SignUp.this,"Failed to register! Try again!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(Activity_SignUp.this,"Failed to register the user!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
