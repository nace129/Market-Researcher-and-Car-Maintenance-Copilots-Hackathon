package com.example.market_researcher_and_car_maintenance_copilots;

import android.content.Intent;
import android.os.Bundle;

import com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard.DocumentUploadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.market_researcher_and_car_maintenance_copilots.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Adding SoundAnalysisFragment to AppBarConfiguration
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_sound_analysis
//        ).build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_sound_analysis,
                R.id.navigation_thinkpdf
                // Do not include ThinkPDF tab here
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home,  R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

// Custom behavior for BottomNavigationView
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_thinkpdf) {
                Intent intent = new Intent(this, DocumentUploadActivity.class);
                startActivity(intent);
                return false; // Don't change the nav destination
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
        });
    }
}
