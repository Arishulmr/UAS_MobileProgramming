package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class EditFoodActivity extends AppCompatActivity {

    private Button uploadButton, backButton, updateButton;
//    private ImageView foodImagePreview;
    private EditText editName, editWeight, editPrice, editQuantity,
        editDesc;

    Spinner editType, editCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        uploadButton = findViewById(R.id.uploadPictureButton);
        backButton = findViewById(R.id.backButton);
        updateButton = findViewById(R.id.updateButton);

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditFoodActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Retrieve data from the intent
        Intent intent = getIntent();
        String foodId = intent.getStringExtra("food_id");
        String foodName = intent.getStringExtra("food_name");
        String foodCategory = intent.getStringExtra("food_category");
        String foodType = intent.getStringExtra("food_type");
        double foodPrice = intent.getDoubleExtra("food_price", 0.0);
        int foodWeight = intent.getIntExtra("food_weight", 0);
        int foodQuantity = intent.getIntExtra("food_quantity", 0);
        String foodDesc = intent.getStringExtra("food_desc");
        String foodImage = intent.getStringExtra("food_image");

        int categoryPosition = intent.getIntExtra("food_category_position", 0);
        int typePosition = intent.getIntExtra("food_type_position", 0);

        // Populate the edit fields in EditFoodActivity
        // Example: EditText editName = findViewById(R.id.editName);
        // editName.setText(foodName);
        editName = findViewById(R.id.foodName);
        editName.setText(foodName);
        editCategory = findViewById(R.id.foodCategory);
        editCategory.setSelection(categoryPosition);
        editWeight = findViewById(R.id.foodWeight);
        editWeight.setText(String.valueOf(foodWeight));
        editPrice = findViewById(R.id.foodPrice);
        editPrice.setText(String.valueOf(foodPrice));
        editQuantity = findViewById(R.id.foodQuantity);
        editQuantity.setText(String.valueOf(foodQuantity));
        editDesc = findViewById(R.id.foodDesc);
        editDesc.setText(foodDesc);
        editType = findViewById(R.id.foodType);

        ImageView foodImagePreview = findViewById(R.id.foodImagePreview);
        Glide.with(this)
                .load(foodImage)
                .placeholder(R.drawable.burger)
                .into(foodImagePreview);
        populateTypeSpinner(foodCategory);
        editType.setSelection(typePosition);



    }
    private void populateTypeSpinner(String selectedCategory){
//        editType.setClickable(true);
        if (Objects.equals(selectedCategory, "Food")){
            ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.food_type_array, android.R.layout.simple_spinner_item);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editType.setAdapter(typeAdapter);
        } else if (Objects.equals(selectedCategory, "Beverage")) {
            ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.beverage_type_array, android.R.layout.simple_spinner_item);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editType.setAdapter(typeAdapter);
        }
    }

}