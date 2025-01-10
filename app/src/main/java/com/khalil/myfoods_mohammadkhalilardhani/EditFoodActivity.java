package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditFoodActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.1.11:8080/myfoods_backend/";
    private static final String IMAGE_UPLOAD_URL =
            "http://192.168.1.11:8080/myfoods_backend/uploads/";
    private Spinner categorySpinner, typeSpinner;
    private Uri selectedImageUri;

    private FoodApi foodApi;

    private String uploadedImageUrl;




    private Button uploadPictureButton, backButton, updateButton;

    private ImageView foodImagePreview;
private Context context;


    private EditText editName, editWeight, editPrice, editQuantity,
        editDesc;

    Spinner editType, editCategory;

    String selectedEditCategory, imageFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        context = this;
        foodImagePreview = findViewById(R.id.foodImagePreview);
        uploadPictureButton = findViewById(R.id.uploadPictureButton);
        backButton = findViewById(R.id.backButton);

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        foodImagePreview.setImageURI(selectedImageUri);
                        uploadImageToServer(selectedImageUri);
                    }
                });

        uploadPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        updateButton = findViewById(R.id.updateButton);
        FoodItem foodItem = (FoodItem) getIntent().getSerializableExtra("foodItem");
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditFoodActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });





        // Retrieve data from the intent
        Intent intent = getIntent();
        int foodId = intent.getIntExtra("food_id", 0);
        String foodName = intent.getStringExtra("food_name");
        String foodCategory = intent.getStringExtra("food_category");
        String foodType = intent.getStringExtra("food_type");
        int foodPrice = intent.getIntExtra("food_price", 0);
        int foodWeight = intent.getIntExtra("food_weight", 0);
        int foodQuantity = intent.getIntExtra("food_quantity", 0);
        String foodDesc = intent.getStringExtra("food_desc");
        String foodImage = intent.getStringExtra("food_image");

        int categoryPosition = intent.getIntExtra("food_category_position", 0);
        int typePosition = intent.getIntExtra("food_type_position", 0);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        foodApi = retrofit.create(FoodApi.class);

        editName = findViewById(R.id.foodName);
        editCategory = findViewById(R.id.foodCategory);
        editWeight = findViewById(R.id.foodWeight);
        editQuantity = findViewById(R.id.foodQuantity);
        editPrice = findViewById(R.id.foodPrice);
        editDesc = findViewById(R.id.foodDesc);
        editType = findViewById(R.id.foodType);
        ImageView foodImagePreview = findViewById(R.id.foodImagePreview);
        editName.setText(foodName);
        editCategory.setSelection(categoryPosition);
        editWeight.setText(String.valueOf(foodWeight));
        editPrice.setText(String.valueOf(foodPrice));
        editQuantity.setText(String.valueOf(foodQuantity));
        editDesc.setText(foodDesc);
        editType.setSelection(typePosition);

        Glide.with(this)
                .load(foodImage)
                .placeholder(R.drawable.burger)
                .into(foodImagePreview);

        editCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEditCategory = editCategory.getSelectedItem().toString();
                populateTypeSpinner(selectedEditCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEditCategory = null;
            }
        });



        updateButton.setOnClickListener(view -> {
            try {
                String updatedName = editName.getText().toString();
                String updatedCategory = editCategory.getSelectedItem().toString();
                String updatedType = editType.getSelectedItem().toString();
                int updatedWeight = Integer.parseInt(editWeight.getText().toString());
                int updatedPrice = Integer.parseInt(editPrice.getText().toString()); // Parse as double
                int updatedQuantity = Integer.parseInt(editQuantity.getText().toString());
                String updatedDesc = editDesc.getText().toString();
                String updatedImage = imageFileName;

                // Call updateFoodItem with appropriate parameters
                updateFoodItem(foodId, updatedName, updatedCategory, updatedType,
                        updatedWeight, updatedPrice, updatedQuantity,
                        updatedDesc, updatedImage);
            } catch (NumberFormatException e) {
                // Handle invalid input gracefully
                e.printStackTrace();
                Toast.makeText(this, "Please enter valid numeric values.", Toast.LENGTH_SHORT).show();
            }


        });






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

    private void updateFoodItem(int foodId, String food_name, String food_category,
                                String food_type,
                                int food_weight, int food_price, int food_quantity, String food_desc,
                                String food_image) {
        String url = "http://192.168.1.11:8080/myfoods_backend/update_item.php";

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("food_id", foodId);
            jsonParams.put("food_name", food_name);
            jsonParams.put("food_image", food_image);
            jsonParams.put("food_desc", food_desc);
            jsonParams.put("food_category",food_category);
            jsonParams.put("food_type", food_type);
            jsonParams.put("food_price", food_price);
            jsonParams.put("food_weight", food_weight);
            jsonParams.put("food_quantity", food_quantity);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                response -> {
                    try {
                        Log.d("EditFoodActivity", "Response: " + response.toString());
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Log.d("EditFoodActivity", "Food updated successfully.");
                        } else {
                            Log.d("EditFoodActivity", "Failed to update food.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
        Toast.makeText(this, "Item updated successfully.", Toast.LENGTH_SHORT).show();
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
            // If the file path couldn't be resolved, return null or handle gracefully
            Toast.makeText(this, "Invalid file path", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void uploadImageToServer(Uri imageUri) {
        try {
            File imageFile = getFileFromUri(imageUri);
            // Convert the URI to a bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Compress the bitmap to a byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] byteArray = stream.toByteArray();

            // Create the request body
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

            // Prepare the multipart request
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

            imageFileName = IMAGE_UPLOAD_URL + imageFile.getName();

            // Set up Retrofit and make the call
            Call<ImageResponse> call = foodApi.uploadImage(body);
            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedImageUrl = response.body().getImageUrl();

                        Glide.with(EditFoodActivity.this)
                                .load(uploadedImageUrl)
                                .placeholder(R.drawable.burger) // Optional placeholder
                                .into(foodImagePreview);

                        foodImagePreview.setImageURI(selectedImageUri);

                        Toast.makeText(EditFoodActivity.this, "Image uploaded!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditFoodActivity.this, "Failed to upload image",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(EditFoodActivity.this, "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uploadedImageUrl", uploadedImageUrl);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uploadedImageUrl = savedInstanceState.getString("uploadedImageUrl");
        if (uploadedImageUrl != null) {
            Glide.with(this)
                    .load(uploadedImageUrl)
                    .into(foodImagePreview);
        }
    }



}

