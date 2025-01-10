package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    private static final String BASE_URL = "@string/base_url";
    private static final String USER_API_URL = BASE_URL + "get_user_data.php";
    private static final String IMAGE_UPLOAD_URL = BASE_URL + "uploads/";

    private EditText usernameEditText, emailEditText;
    private ImageView profileImageView;
    private Context context;
    private Button uploadPictureButton, updateButton, backButton;

    private Uri selectedImageUri;
    private String uploadedImageUrl, imageFileName;


    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        profileImageView = findViewById(R.id.profileImageView);
        uploadPictureButton = findViewById(R.id.uploadPictureButton);
        updateButton = findViewById(R.id.updateButton);
        backButton = findViewById(R.id.backButton);
        int userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userApi = retrofit.create(UserApi.class);

        fetchUserData(userId);

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profileImageView.setImageURI(selectedImageUri);
                        uploadImageToServer(selectedImageUri);
                    }
                });

        uploadPictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        updateButton.setOnClickListener(v -> updateUserProfile(userId));

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserData(int userId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, USER_API_URL + "?user_id=" + userId,
                null,
                response -> {
                    try {
                        String username = response.getString("username");
                        String email = response.getString("email");
                        String profileImage = response.getString("profile_image");

                        usernameEditText.setText(username);
                        emailEditText.setText(email);

                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.profileplaceholder)
                                .into(profileImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing user data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
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
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(),
                    requestFile);

            imageFileName = IMAGE_UPLOAD_URL + imageFile.getName();


            Call<ImageResponse> call = userApi.uploadProfileImage(body);
            call.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedImageUrl = response.body().getImageUrl();
                        Glide.with(ProfileActivity.this)
                                .load(uploadedImageUrl)
                                .placeholder(R.drawable.profileplaceholder)
                                .into(profileImageView);

                        profileImageView.setImageURI(selectedImageUri);

                        Toast.makeText(ProfileActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Image upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile(int userId) {
        String updatedUsername = usernameEditText.getText().toString();
        String updatedEmail = emailEditText.getText().toString();
        String url = "@string/base_url" + "update_user_data.php";

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("user_id", userId);
            jsonParams.put("user_name", updatedUsername);
            jsonParams.put("user_email", updatedEmail);
            jsonParams.put("user_image", imageFileName);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                    jsonParams,
                    response -> {
                        try {
                            Log.d("ProfileActivity", "Response: " + response.toString());
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error updating profile.", Toast.LENGTH_SHORT).show();
                    });

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);
            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating update request.", Toast.LENGTH_SHORT).show();
        }
    }
}
