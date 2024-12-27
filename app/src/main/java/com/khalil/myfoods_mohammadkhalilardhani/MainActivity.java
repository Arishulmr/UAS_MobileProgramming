package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton addFoodButton;

    private RecyclerView foodRecyclerView;
    private FoodItemAdapter adapter;
    private List<FoodItem> foodItems = new ArrayList<>();

    private Retrofit retrofit;
    private FoodApi foodApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addFoodButton = (ImageButton) findViewById(R.id.addItemButton);
        addFoodButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
            startActivity(intent);
            finish();
        });

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.10:8080/myfoods_backend/") // Your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foodApi = retrofit.create(FoodApi.class);

        // Set up the adapter
        adapter = new FoodItemAdapter(foodItems);
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
