    package com.khalil.myfoods_mohammadkhalilardhani;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.text.TextUtils;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Spinner;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.AppCompatButton;

    import java.io.ByteArrayOutputStream;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import java.util.Objects;

    import okhttp3.MediaType;
    import okhttp3.MultipartBody;
    import okhttp3.RequestBody;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class AddFoodActivity extends AppCompatActivity {
        private ImageView foodImagePreview;
        private Uri selectedImageUri;
        private String uploadedImageUrl;

        private EditText foodNameEditText, foodWeightEditText, foodPriceEditText, foodQuantityEditText, foodDescEditText;
        private Spinner categorySpinner, typeSpinner;
        private AppCompatButton submitButton, backButton;

        private String selectedCategory, selectedType, imageFileName;

        private static final String BASE_URL = "http://192.168.1.12:8080/myFoods_backend/";
        private FoodApi foodApi;

        @SuppressLint("WrongViewCast")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_food);

            foodImagePreview = findViewById(R.id.foodImagePreview);
            Button uploadPictureButton = findViewById(R.id.uploadPictureButton);

            ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            foodImagePreview.setVisibility(View.VISIBLE);
                            foodImagePreview.setImageURI(selectedImageUri);
                            uploadImageToServer(selectedImageUri);
                        }
                    });

            uploadPictureButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            });


            foodNameEditText = findViewById(R.id.foodName);
            foodWeightEditText = findViewById(R.id.foodWeight);
            foodPriceEditText = findViewById(R.id.foodPrice);
            foodQuantityEditText = findViewById(R.id.foodQuantity);
            foodDescEditText = findViewById(R.id.foodDesc);
            submitButton = findViewById(R.id.submitFoodButton);
            backButton = findViewById(R.id.backButton);

            categorySpinner = findViewById(R.id.foodCategory);
            typeSpinner = findViewById(R.id.foodType);

            ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                    R.array.category_array, android.R.layout.simple_spinner_item);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(categoryAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedCategory = parentView.getItemAtPosition(position).toString();
                    populateTypeSpinner(selectedCategory);
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


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            foodApi = retrofit.create(FoodApi.class);

            backButton.setOnClickListener(v -> {
                        Intent intent = new Intent(AddFoodActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });

            submitButton.setOnClickListener(v -> {
                String foodName = foodNameEditText.getText().toString().trim();
                String weightStr = foodWeightEditText.getText().toString().trim();
                String priceStr = foodPriceEditText.getText().toString().trim();
                String quantityStr = foodQuantityEditText.getText().toString().trim();
                String description = foodDescEditText.getText().toString().trim();

                if (selectedImageUri != null) {
                    addFoodItemWithImageUrl();

                }

                else if (isValidInput(foodName, weightStr, priceStr, quantityStr)) {
                    int weight = Integer.parseInt(weightStr);
                    int price = Integer.parseInt(priceStr);
                    int quantity = Integer.parseInt(quantityStr);
                    String imageUrl = null;


                    FoodItem foodItem = new FoodItem(foodName, selectedCategory, selectedType, weight
                            , price, quantity, description, imageUrl);

                    addFoodItem(foodItem);
                    foodImagePreview.setVisibility(View.GONE);
                    Log.d("AddFoodActivity", "Selected Type: " + selectedType);

                }
            });
        }

        private void populateTypeSpinner(String selectedCategory){
            typeSpinner.setClickable(true);
            if (Objects.equals(selectedCategory, "Food")){
                ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                        R.array.food_type_array, android.R.layout.simple_spinner_item);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(typeAdapter);
            } else if (Objects.equals(selectedCategory, "Beverage")) {
                ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                        R.array.beverage_type_array, android.R.layout.simple_spinner_item);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(typeAdapter);
            }
        }


        private File getFileFromUri(Uri uri) {
            String filePath = null;
            String[] projection = {MediaStore.Images.Media.DATA};
            try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            }

            if (filePath != null) {
                return new File(filePath);
            } else {
                Toast.makeText(this, "Invalid file path", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        private void uploadImageToServer(Uri imageUri) {
            try {
                File imageFile = getFileFromUri(imageUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byteArray = stream.toByteArray();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

                MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

                imageFileName = imageFile.getName();

                Call<ImageResponse> call = foodApi.uploadImage(body);
                call.enqueue(new Callback<ImageResponse>() {
                    @Override
                    public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            uploadedImageUrl = response.body().getImageUrl();
                            Toast.makeText(AddFoodActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddFoodActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageResponse> call, Throwable t) {
                        Toast.makeText(AddFoodActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }


        private File copyUriToFile(Uri uri) {
            try {
                File tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());
                try (InputStream inputStream = getContentResolver().openInputStream(uri);
                     OutputStream outputStream = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return tempFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }



        private void addFoodItemWithImageUrl() {
            String foodName = foodNameEditText.getText().toString().trim();
            String weightStr = foodWeightEditText.getText().toString().trim();
            String priceStr = foodPriceEditText.getText().toString().trim();
            String quantityStr = foodQuantityEditText.getText().toString().trim();
            String description = foodDescEditText.getText().toString().trim();

            if (isValidInput(foodName, weightStr, priceStr, quantityStr)) {
                int weight = Integer.parseInt(weightStr);
                int price = Integer.parseInt(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                String imageUrl = imageFileName;

                FoodItem foodItem = new FoodItem(foodName, selectedCategory, selectedType, weight,
                        price, quantity, description, imageUrl);

                addFoodItem(foodItem);
                foodImagePreview.setVisibility(View.GONE);
            }
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

        private void addFoodItem(FoodItem foodItem) {
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
            foodImagePreview.setImageDrawable(null);
        }
    }
