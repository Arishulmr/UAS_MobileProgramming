package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FoodItemAdapter.OnItemClickListener {
    private ImageButton addFoodButton, menuButton;
    private EditText searchBar;
    private TextView searchButton;
private Button clearFilterButton;
    private RecyclerView foodRecyclerView;
    private FoodItemAdapter adapter, modalAdapter;
    private List<FoodItem> foodItems = new ArrayList<>();

    private Retrofit retrofit;
    private FoodApi foodApi;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchText);

        searchButton.setOnClickListener(v -> {
            searchData(searchBar.getText().toString());
                });


        clearFilterButton = findViewById(R.id.clearFilterButton);
        menuButton = findViewById(R.id.menuButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        addFoodButton = findViewById(R.id.addItemButton);
        addFoodButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
            startActivity(intent);
            finish();
        });

        clearFilterButton.setOnClickListener(v -> {
            fetchFoodItems();
            clearFilterButton.setVisibility(View.GONE);
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
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_filter) {
                showFilterDialog();
            } else if (id == R.id.nav_sort_by) {
                showSortDialog();
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        retrofit = new Retrofit.Builder()
                .baseUrl("@string/base_url")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foodApi = retrofit.create(FoodApi.class);
        adapter = new FoodItemAdapter(foodItems, this);
        foodRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::showItemDetailsDialog);

        fetchFoodItems();
    }

    private void searchData(String input) {
        if (input == null || input.trim().isEmpty()) {
            fetchFoodItems();
            return;
        }

        foodApi.searchFood(input).enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    if (!foodItemsFromApi.isEmpty()) {
                        foodItems.clear();
                        foodItems.addAll(foodItemsFromApi);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MainActivity", "Response was unsuccessful or empty.");
                    Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onItemClick(FoodItem item) {
        showItemDetailsDialog(item);
    }

    private void showItemDetailsDialog(FoodItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getFood_name());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_item_details, null);
        builder.setView(dialogView);

        TextView itemCategory = dialogView.findViewById(R.id.itemCategory);
        TextView itemType = dialogView.findViewById(R.id.itemType);
        TextView itemPrice = dialogView.findViewById(R.id.itemPrice);
        TextView itemWeight = dialogView.findViewById(R.id.itemWeight);
        TextView itemQuantity = dialogView.findViewById(R.id.itemQuantity);
        TextView itemDescription = dialogView.findViewById(R.id.itemDescription);
        ImageView itemImage = dialogView.findViewById(R.id.itemImage);

        itemDescription.setText(item.getFood_desc());
        itemCategory.setText(item.getFood_category());
        itemType.setText(item.getFood_type());
        itemPrice.setText(String.valueOf(item.getFood_price()));
        itemWeight.setText(String.valueOf(item.getFood_weight()));
        itemQuantity.setText(String.valueOf(item.getFood_quantity()));
      Glide.with(this)
              .load(item.getFood_image())
              .placeholder(R.drawable.burger)
              .into(itemImage);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton("Edit", (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, EditFoodActivity.class);
            intent.putExtra("food_id", item.getFood_id());
            intent.putExtra("food_name", item.getFood_name());
            intent.putExtra("food_category", item.getFood_category());
            intent.putExtra("food_type", item.getFood_type());
            intent.putExtra("food_price", item.getFood_price());
            intent.putExtra("food_weight", item.getFood_weight());
            intent.putExtra("food_quantity", item.getFood_quantity());
            intent.putExtra("food_desc", item.getFood_desc());
            intent.putExtra("food_image", item.getFood_image());

            intent.putExtra("food_category_position", getCategoryPosition(item.getFood_category()));

            if(item.getFood_category().equals("Food")){
                intent.putExtra("food_type_position", getFoodTypePosition(item.getFood_type()));
            }else{
                intent.putExtra("food_type_position", getBeverageTypePosition(item.getFood_type()));
            }
            startActivity(intent);
            finish();
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getCategoryPosition(String category) {
        String[] categories = {"Food", "Beverage"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return 0;
    }

    private int getFoodTypePosition(String type) {
        String[] types = {"Fruits and Vegetables", "Carbohydrates", "Proteins", "Fats and Oils"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                Log.d("MainActivity", "getFoodTypePosition: " + i);
                return i;
            }
        }
        return 0;
    }

    private int getBeverageTypePosition(String type) {

        String[] types = {"Carbonated", "Coffee", "Tea", "Sports", "Water", "Juice"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                Log.d("MainActivity", "getBeverageTypePosition: " + i);

                return i;
            }
        }
        return 0;
    }

    private void showClearFilterButton(){
        clearFilterButton.setVisibility(View.VISIBLE);
    }
    private void showFilterDialog() {
        String[] filterOptions = {"Favorited", "Have image", "Food", "Beverage"};
        final int[] selectedOption = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Filter")
                .setSingleChoiceItems(filterOptions, selectedOption[0], (dialog, which) -> selectedOption[0] = which)
                .setPositiveButton("Apply", (dialog, id) -> {
                    if (selectedOption[0] == -1) {
                        Toast.makeText(this, "No filter selected!", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (selectedOption[0]) {
                            case 0:
                                applyFilter("favorited");
                                break;
                            case 1:
                                applyFilter("have_image");
                                break;
                            case 2:
                                applyFilter("Food");
                                break;
                            case 3:
                                applyFilter("Beverage");
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void applyFilter(String keyWord) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("keyWord", keyWord);

        foodApi.getFilteredItems(requestBody).enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();
                    foodItems.addAll(foodItemsFromApi);
                    adapter.notifyDataSetChanged();
                    showClearFilterButton();
                } else {
                    Log.e("API Error", "Response unsuccessful or empty.");
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });

    }

    private void applySort(String keyWord) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("keyWord", keyWord);

        foodApi.getSortedItems(requestBody).enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();
                    foodItems.addAll(foodItemsFromApi);
                    adapter.notifyDataSetChanged();
                    showClearFilterButton();
                } else {
                    Log.e("API Error", "Response unsuccessful or empty.");
                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {
                Log.e("API Error", "Request failed: " + t.getMessage());
            }
        });

    }



    private void showSortDialog() {
        String[] filterOptions = {"Name", "Price",
                "Category", "Weight", "Quantity"};
        final int[] selectedOption = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Filter")
                .setSingleChoiceItems(filterOptions, selectedOption[0], (dialog, which) -> {
                    selectedOption[0] = which;
                })
                .setPositiveButton("Apply", (dialog, id) -> {
                    if (selectedOption[0] == 0) {
                        applySort("food_name");
                    } else if (selectedOption[0] == 1) {
                        applySort("food_price");

                    } else if (selectedOption[0] == 2) {
                        applySort("food_category");

                    }else if (selectedOption[0] == 3) {
                        applySort("food_weight");
                    } else if (selectedOption[0] == 4) {
                        applySort("food_quantity");

                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void fetchFoodItems() {
        foodApi.getFoodItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();
                    foodItems.addAll(foodItemsFromApi);
                    adapter.notifyDataSetChanged();
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
