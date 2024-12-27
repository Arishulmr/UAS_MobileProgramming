package com.khalil.myfoods_mohammadkhalilardhani;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.gson.Gson;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFoodActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView foodImagePreview;
    private Uri imageUri;

    private EditText foodNameEditText, foodWeightEditText, foodPriceEditText, foodQuantityEditText, foodDescEditText;
    private Spinner categorySpinner, typeSpinner;
    private AppCompatButton submitButton;

    private String selectedCategory, selectedType;

    private static final String BASE_URL = "http://192.168.1.10:8080/myfoods_backend/";
    private FoodApi foodApi;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodImagePreview = findViewById(R.id.foodImagePreview);
        Button uploadPictureButton = findViewById(R.id.uploadPictureButton);
        Button backButton = findViewById(R.id.backButton);
        foodNameEditText = findViewById(R.id.foodName);
        foodWeightEditText = findViewById(R.id.foodWeight);
        foodPriceEditText = findViewById(R.id.foodPrice);
        foodQuantityEditText = findViewById(R.id.foodQuantity);
        foodDescEditText = findViewById(R.id.foodDesc);
        submitButton = findViewById(R.id.submitFoodButton);

        categorySpinner = findViewById(R.id.foodCategory);
        typeSpinner = findViewById(R.id.foodType);

        // Upload Picture Logic
        uploadPictureButton.setOnClickListener(v -> openImageChooser());
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Handle the result here
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        // Do something with the selected image URI
                    }
                });
        // Back Button Logic
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(AddFoodActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up the category Spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Set up the type Spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCategory = null;
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedType = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedType = null;
            }
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        foodApi = retrofit.create(FoodApi.class);

        // Set up the submit button
        submitButton.setOnClickListener(v -> {
            String foodName = foodNameEditText.getText().toString().trim();
            String weightStr = foodWeightEditText.getText().toString().trim();
            String priceStr = foodPriceEditText.getText().toString().trim();
            String quantityStr = foodQuantityEditText.getText().toString().trim();
            String description = foodDescEditText.getText().toString().trim();

            if (isValidInput(foodName, weightStr, priceStr, quantityStr)) {
                int weight = Integer.parseInt(weightStr);
                int price = Integer.parseInt(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                FoodItem foodItem = new FoodItem(foodName, selectedCategory, selectedType, weight, price, quantity, description, null);

                if (imageUri != null) {
                    addFoodItemWithImage(foodItem, imageUri); // Handle image upload
                } else {
                    addFoodItem(foodItem); // No image to upload
                }
            }
        });
    }

    private boolean isValidInput(String foodName, String weight, String price, String quantity) {
        if (TextUtils.isEmpty(foodName)) {
            Toast.makeText(this, "Food name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(weight) || Integer.parseInt(weight) <= 0) {
            Toast.makeText(this, "Weight must be a positive number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(price) || Integer.parseInt(price) <= 0) {
            Toast.makeText(this, "Price must be a positive number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(quantity) || Integer.parseInt(quantity) <= 0) {
            Toast.makeText(this, "Quantity must be a positive number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);  // Launch the intent using the new method
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            foodImagePreview.setImageURI(imageUri);
            foodImagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void addFoodItemWithImage(FoodItem foodItem, Uri imageUri) {
        try {
            // Step 1: Prepare the image file
            File file = new File(getPath(this, imageUri));
            RequestBody imageRequestBody = RequestBody.Companion.create(file, MediaType.get("image/*"));
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), imageRequestBody);

            // Step 2: Prepare the food data as JSON string
            Gson gson = new Gson();
            String foodDataJson = gson.toJson(foodItem);
            RequestBody foodDataRequestBody = RequestBody.Companion.create(foodDataJson, MediaType.get("application/json"));

            // Step 3: Make the request
            Call<Void> call = foodApi.addFoodItemWithImage(foodDataRequestBody, imagePart);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddFoodActivity.this, "Food item with image added successfully", Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Toast.makeText(AddFoodActivity.this, "Failed to add food item with image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AddFoodActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("Upload", "Error preparing image: " + e.getMessage());
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }






    private void addFoodItem(FoodItem foodItem) {
        // Make the API call to add food item without image
        Call<Void> call = foodApi.addFoodItem(foodItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddFoodActivity.this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(AddFoodActivity.this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddFoodActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        foodNameEditText.setText("");
        foodWeightEditText.setText("");
        foodPriceEditText.setText("");
        foodQuantityEditText.setText("");
        foodDescEditText.setText("");
        categorySpinner.setSelection(0);
        typeSpinner.setSelection(0);
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
}
