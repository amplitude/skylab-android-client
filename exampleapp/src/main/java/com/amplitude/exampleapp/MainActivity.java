package com.amplitude.exampleapp;

import android.os.Bundle;

import com.amplitude.skylab.Skylab;
import com.amplitude.skylab.SkylabClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SkylabClient client = Skylab.getInstance();
        Future<SkylabClient> refetchFuture = client.refetchAll();

        String variant = client.getVariant("demo-notifications");
        if (variant.equals("true")) {
            navView.getMenu().findItem(R.id.navigation_notifications).setVisible(true);
        } else {
            navView.getMenu().findItem(R.id.navigation_notifications).setVisible(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
