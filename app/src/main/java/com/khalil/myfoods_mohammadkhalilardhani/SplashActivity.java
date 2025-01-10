package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
        String animationUrl = "https://assets10.lottiefiles.com/packages/lf20_5ngs2ksb.json";
        animationView.setAnimationFromUrl(animationUrl);
        animationView.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }, 4000); // 4 seconds delay
    }
}
