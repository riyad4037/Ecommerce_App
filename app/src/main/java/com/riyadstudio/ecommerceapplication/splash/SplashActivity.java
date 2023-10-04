package com.riyadstudio.ecommerceapplication.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.auth.FirebaseAuth;
import com.riyadstudio.ecommerceapplication.DataRepository.FirebaseAuthRepo;
import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.view.AuthActivity;
import com.riyadstudio.ecommerceapplication.view.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = new FirebaseAuthRepo(this).getFirebaseAuth();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(firebaseAuth.getCurrentUser()!=null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);
    }
}