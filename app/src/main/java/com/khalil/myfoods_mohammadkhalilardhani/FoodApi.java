package com.khalil.myfoods_mohammadkhalilardhani;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;

// API interface for Retrofit
public interface FoodApi {
    @GET("fetch_food_items.php")  // Ensure this URL path is correct
    Call<List<FoodItem>> getFoodItems();
    @POST("add_food_item.php")  // Your server's endpoint for adding food items
    Call<Void> addFoodItem(@Body FoodItem foodItem);

    @Multipart
    @POST("upload_image.php")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part file);
}
