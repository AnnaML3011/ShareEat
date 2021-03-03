package com.example.shareeat.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.shareeat.utils.AppManager;
import com.example.shareeat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Activity_Main extends AppCompatActivity implements View.OnClickListener {
    private static final String A_TAG = "A_tag";
    private AppManager appManager;
    private FirebaseAuth mAuth;
    private Button login_BTN;
    private Button signup_manualy_BTN;
    private EditText login_email_LBL;
    private EditText login_password_LBL;
    private String entered_email;
    private String entered_pass;


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
                break;
            case R.id.signup_manualy_BTN:
                startActivity(new Intent(Activity_Main.this,Activity_SignUp.class));
                break;
        }
    }


    private void initViews(){
        login_BTN = appManager.getLogin_BTN();
        signup_manualy_BTN = appManager.getSignup_manualy_BTN();
        login_email_LBL = appManager.getLogin_email_LBL();
        login_password_LBL= appManager.getLogin_password_LBL();
        login_BTN.setOnClickListener(this);
        signup_manualy_BTN.setOnClickListener(this);
    }


    private void loginUser() {
        entered_email = login_email_LBL.getText().toString();
        entered_pass = login_password_LBL.getText().toString();
        if (entered_email.isEmpty() || entered_pass.isEmpty()) {
            makeToast("Email or password is empty, please fill in the empty fields!");
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(entered_email).matches()) {
            Toast.makeText(getBaseContext(),"Please provide valid email!",
                    Toast.LENGTH_LONG).show();
            return;
        }else {
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
                            intent.putExtra(A_TAG,"Activity_Main");
                            startActivity(intent);
//                            readUserFromDB(user.getUid());
                       } else {
                            // If the system is unable to find the user, it issues an error message accordingly
                            if (task.getException().getMessage().contains("email")) {
                                login_email_LBL.setError(task.getException().getMessage());
                                login_email_LBL.requestFocus();
                                makeToast("Cant find user with email:" + login_email_LBL.getText().toString()+"!");
                            } else if (task.getException().getMessage().contains("password")) {
                                login_password_LBL.setError(task.getException().getMessage());
                                login_password_LBL.requestFocus();
                                makeToast("Incorrect password!");
                            } else if (task.getException().getMessage().contains("user")) {
                                login_email_LBL.setError(task.getException().getMessage());
                                login_email_LBL.requestFocus();
                                makeToast("No such user!");
                            }
                        }
                    }
                });
    }

    void makeToast(String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),string,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}