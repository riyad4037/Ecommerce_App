package com.riyadstudio.ecommerceapplication.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.view.fragment.LoginNavFragment;

public class AuthActivity extends AppCompatActivity {

    private FrameLayout authFrame;

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        authFrame = findViewById(R.id.AuthFrameLayout);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.AuthFrameLayout, new LoginNavFragment());
        fragmentTransaction.commit();
    }
}