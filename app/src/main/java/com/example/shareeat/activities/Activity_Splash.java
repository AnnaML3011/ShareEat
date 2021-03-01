package com.example.shareeat.activities;
import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shareeat.R;
import java.util.concurrent.Executor;

public class Activity_Splash extends AppCompatActivity {

    private final int ANIMATION_DURATION = 3000;
    private ImageView splash_IMG_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        findViews();
        startAnimation(splash_IMG_logo);
    }

    private void startApp() {
        Intent intent = new Intent(getBaseContext(), Activity_Main.class);
        startActivity(intent);
        finish();
    }

    private void startAnimation(View view) {
        final ScaleAnimation anim = new ScaleAnimation(0.4f, 1f, 0.4f, 1f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 0.1f);
        anim.setDuration(ANIMATION_DURATION);
        view.setAnimation(anim);
//        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(4500);
//        rotate.setInterpolator(new LinearInterpolator());
//        view.startAnimation(rotate);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        view.setY(-height);
        view.animate()
                .translationY(0)
                .setDuration(ANIMATION_DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startApp();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }


    private void findViews() {
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }
}
