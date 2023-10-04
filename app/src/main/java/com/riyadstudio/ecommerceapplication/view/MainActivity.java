package com.riyadstudio.ecommerceapplication.view;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.view.fragment.CategoriesFragment;
import com.riyadstudio.ecommerceapplication.view.fragment.FavouritesFragment;
import com.riyadstudio.ecommerceapplication.view.fragment.HomeFragment;
import com.riyadstudio.ecommerceapplication.view.fragment.UserAccountFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    private FrameLayout frameLayout;

    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = findViewById(R.id.FrameLayoutInMainActivity);


        floatingActionButton = findViewById(R.id.CartFloatingActionButton);

        bottomNavigationView = findViewById(R.id.BottomNavigationViewBar);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        fragmentMap.put(R.id.homeItemMenu, new HomeFragment());
        fragmentMap.put(R.id.CategoryItemMenu, new CategoriesFragment());
        fragmentMap.put(R.id.FavouriteItemMenu, new FavouritesFragment());
        fragmentMap.put(R.id.ProfileItemMenu, new UserAccountFragment());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = fragmentMap.get(item.getItemId());

                if (selectedFragment!=null) {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.FrameLayoutInMainActivity, selectedFragment);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FrameLayoutInMainActivity, new HomeFragment());
        fragmentTransaction.commit();
    }
}