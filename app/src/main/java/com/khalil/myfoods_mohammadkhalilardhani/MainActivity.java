package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton addFoodButton, menuButton;

    private RecyclerView foodRecyclerView;
    private FoodItemAdapter adapter;
    private List<FoodItem> foodItems = new ArrayList<>();

    private Retrofit retrofit;
    private FoodApi foodApi;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        menuButton = findViewById(R.id.menuButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        addFoodButton = findViewById(R.id.addItemButton);
        addFoodButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
            startActivity(intent);
            finish();
        });

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Toast.makeText(this, "profile clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_filter) {
                Toast.makeText(this, "filter clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_sort_by) {
                Toast.makeText(this, "sort clicked", Toast.LENGTH_SHORT).show();

                // Handle the sort by action
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "logout clicked", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.9:8080/myfoods_backend/") // Your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foodApi = retrofit.create(FoodApi.class);
        // Set up the adapter
        adapter = new FoodItemAdapter(foodItems, getApplicationContext());
        foodRecyclerView.setAdapter(adapter);

        // Fetch food items from the API
        fetchFoodItems();
    }


    private void fetchFoodItems() {
        // Use Retrofit to make the API call
        foodApi.getFoodItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();  // Clear existing data
                    foodItems.addAll(foodItemsFromApi);  // Add new data
                    adapter.notifyDataSetChanged();  // Notify adapter of data change
                } else {
                    Log.e("MainActivity", "Response was unsuccessful or empty.");
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Log.e("MainActivity", "Failed to fetch food items: " + t.getMessage());
            }
        });
    }
}
