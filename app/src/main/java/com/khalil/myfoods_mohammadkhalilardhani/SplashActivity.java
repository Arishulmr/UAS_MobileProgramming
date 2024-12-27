package com.khalil.myfoods_mohammadkhalilardhani;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable merge paths for KitKat and above
        LottieDrawable lottieDrawable = new LottieDrawable();
        lottieDrawable.enableMergePathsForKitKatAndAbove(true);

        setContentView(R.layout.activity_splash);
        LottieAnimationView view = findViewById(R.id.lottieAnimationView);

        // Attach animation and play
        view.setFailureListener(error -> Log.e("Lottie", "Animation failed to load", error));
        view.setAnimationFromUrl("https://lottie.host/d68aa9c9-b1f0-4b0c-b223-865784a4d60c/OfAI7tM40e.lottie");
        view.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }, 1000); // 1 seconds delay
    }
}