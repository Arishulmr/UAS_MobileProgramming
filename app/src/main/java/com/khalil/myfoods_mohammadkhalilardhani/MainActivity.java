package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodItemAdapter.OnItemClickListener {
    private ImageButton addFoodButton, menuButton;
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
//                Toast.makeText(this, "profile clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_filter) {
                showFilterDialog();
//                Toast.makeText(this, "filter clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_sort_by) {
                showSortDialog();
//                Toast.makeText(this, "sort clicked", Toast.LENGTH_SHORT).show();
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

        // Set up Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.11:8080/myfoods_backend/") // Your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foodApi = retrofit.create(FoodApi.class);
        // Set up the adapter
        adapter = new FoodItemAdapter(foodItems, this);
        foodRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this::showItemDetailsDialog);

        // Fetch food items from the API
        fetchFoodItems();
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

            // Pass the FoodItem details to EditFoodActivity
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
//            intent.putExtra("food_type_position", getTypePosition(item.getFood_type()));

            if(item.getFood_category().equals("Food")){
//                Toast.makeText(this, item.getFood_type(), Toast.LENGTH_SHORT).show();
                intent.putExtra("food_type_position", getFoodTypePosition(item.getFood_type()));
            }else{
//                Toast.makeText(this, item.getFood_type(), Toast.LENGTH_SHORT).show();
                intent.putExtra("food_type_position", getBeverageTypePosition(item.getFood_type()));
            }
            startActivity(intent);
            finish();
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getCategoryPosition(String category) {
        // Define your categories list (replace with your actual list)
        String[] categories = {"Food", "Beverage"};
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return i;
            }
        }
        return 0; // Default to the first position if not found
    }

    private int getFoodTypePosition(String type) {
        // Define your types list (replace with your actual list)
        String[] types = {"Fruits and Vegetables", "Carbohydrates", "Proteins", "Fats and Oils"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                Log.d("MainActivity", "getFoodTypePosition: " + i);
                return i;
            }
        }
        return 0; // Default to the first position if not found
    }

    private int getBeverageTypePosition(String type) {
        // Define your types list (replace with your actual list)
        String[] types = {"Carbonated", "Coffee", "Tea", "Sports", "Water", "Juice"};
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(type)) {
                Log.d("MainActivity", "getBeverageTypePosition: " + i);

                return i;
            }
        }
        return 0; // Default to the first position if not found
    }

    private void showClearFilterButton(){
        clearFilterButton.setVisibility(View.VISIBLE);
    }
    private void showFilterDialog() {
        // Options for the radio buttons
        String[] filterOptions = {"Favorited", "Have image", "Food", "Beverage"};
        // Variable to track the selected item
        final int[] selectedOption = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Filter")
                .setSingleChoiceItems(filterOptions, selectedOption[0], (dialog, which) -> {
                    // Update the selected option
                    selectedOption[0] = which;
                })
                .setPositiveButton("Apply", (dialog, id) -> {
                    // Handle the filter application based on selectedOption[0]
                    if (selectedOption[0] == 0) {
                        // Apply "Favorited" filter
                        applyFavoritedFilter();
                    } else if (selectedOption[0] == 1) {
                        // Apply "Have image" filter
                        applyHaveImageFilter();
                    } else if (selectedOption[0] == 2) {
                        applyCategoryFoodFilter();
                    }  else if (selectedOption[0] == 3) {
                        applyCategoryBeverageFilter();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.dismiss();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void applyCategoryBeverageFilter(){
        foodApi.getBeverageItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();  // Clear existing data
                    foodItems.addAll(foodItemsFromApi);  // Add new data
                    adapter.notifyDataSetChanged();  // Notify adapter of data change
                    showClearFilterButton();

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

    private void applyCategoryFoodFilter() {
        foodApi.getFoodFoodItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();  // Clear existing data
                    foodItems.addAll(foodItemsFromApi);  // Add new data
                    adapter.notifyDataSetChanged();  // Notify adapter of data change
                    showClearFilterButton();

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

    private void showSortDialog() {
        // Options for the radio buttons
        String[] filterOptions = {"Name", "Price",
                "Category", "Weight", "Quantity"};
        // Variable to track the selected item
        final int[] selectedOption = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Filter")
                .setSingleChoiceItems(filterOptions, selectedOption[0], (dialog, which) -> {
                    // Update the selected option
                    selectedOption[0] = which;
                })
                .setPositiveButton("Apply", (dialog, id) -> {
                    // Handle the filter application based on selectedOption[0]
                    if (selectedOption[0] == 0) {
                        Toast.makeText(this, "Name sort applied", Toast.LENGTH_SHORT).show();
                    } else if (selectedOption[0] == 1) {
                        Toast.makeText(this, "Price sort applied", Toast.LENGTH_SHORT).show();

                    } else if (selectedOption[0] == 2) {
                        Toast.makeText(this, "Category sort applied", Toast.LENGTH_SHORT).show();

                    }else if (selectedOption[0] == 3) {
                        Toast.makeText(this, "Weight sort applied", Toast.LENGTH_SHORT).show();
                    } else if (selectedOption[0] == 4) {
                        Toast.makeText(this, "Quantity sort applied", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.dismiss();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void applyFavoritedFilter() {
        // Implement the logic to filter the list by favorited items
        foodApi.getFavoritedFoodItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();  // Clear existing data
                    foodItems.addAll(foodItemsFromApi);  // Add new data
                    adapter.notifyDataSetChanged();
                    showClearFilterButton();
                // Notify adapter of data change
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

    private void applyHaveImageFilter() {
        foodApi.getImageItems().enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodItem> foodItemsFromApi = response.body();
                    foodItems.clear();  // Clear existing data
                    foodItems.addAll(foodItemsFromApi);  // Add new data
                    adapter.notifyDataSetChanged();  // Notify adapter of data change
                    showClearFilterButton();

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
