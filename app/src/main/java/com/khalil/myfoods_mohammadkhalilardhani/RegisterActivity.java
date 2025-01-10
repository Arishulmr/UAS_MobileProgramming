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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://192.168.1.12:8080/myFoods_backend/";
    private static final String IMAGE_UPLOAD_URL =
            BASE_URL + "uploads/";

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Uri selectedImageUri;
    private FoodApi foodApi;
    private String uploadedImageUrl;

    private String imageFileName;
    private Button registerButton, backButton;
    private ImageView profileImageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        backButton = findViewById(R.id.backButton);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        profileImageView = findViewById(R.id.profileImageView);
        Button uploadPictureButton = findViewById(R.id.uploadPictureButton);

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        foodApi = retrofit.create(FoodApi.class);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(v -> {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                String url = "http://192.168.1.12:8080/myFoods_backend/"+"register.php";
                if (selectedImageUri != null) {
                    addUserWithImageUrl();
                    startActivity(new Intent(RegisterActivity.this,
                            LoginActivity.class));
                    finish();
                } else if (isValidInput(username, email, password)) {
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    if (jsonResponse.getString("status").equals("success")) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", username);
                            params.put("email", email);
                            params.put("password", password);
                            return params;
                        }
                    };

                    queue.add(stringRequest);
                    startActivity(new Intent(RegisterActivity.this,
                            LoginActivity.class));
                    finish();
                }


        });


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

    private boolean isValidInput(String username, String password, String email) {
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addUserWithImageUrl() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (isValidInput(username, email, password)) {

            String imageUrl = IMAGE_UPLOAD_URL + imageFileName;

            User user = new User(username, email, password, imageUrl);

            addUser(user);
            Log.d("RegisterActivity",
                    "imageFileName: " + imageFileName);
        }
    }

    private void addUser(User user){
        Call<Void> call = foodApi.registerWithImage(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User registered successfully",
                            Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register User",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        profileImageView.setImageDrawable(null);
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
                        Toast.makeText(RegisterActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to upload image",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }
}
