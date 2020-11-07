package com.example.gupshup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gupshup.Login.LoginActivity;

public class SplashActivity extends AppCompatActivity
{

    ImageView splashImg;
    TextView splashText;
    Animation splashAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }
        splashImg = findViewById(R.id.splashImg);
        splashText = findViewById(R.id.splashText);
        splashAnimation = AnimationUtils.loadAnimation(this,R.anim.splash_animation);

        splashAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        splashImg.startAnimation(splashAnimation);
        splashText.startAnimation(splashAnimation);
    }
}