package com.khalil.myfoods_mohammadkhalilardhani;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import java.util.List;

// API interface for Retrofit
public interface FoodApi {
    @GET("fetch_food_items.php")  // Ensure this URL path is correct
    Call<List<FoodItem>> getFoodItems();
    @GET("fetch_beverage_items.php")  // Ensure this URL path is correct
    Call<List<FoodItem>> getBeverageItems();
    @GET("fetch_food_food_items.php")  // Ensure this URL path is correct
    Call<List<FoodItem>> getFoodFoodItems();
    @GET("fetch_photo_items.php")
    Call<List<FoodItem>> getImageItems();
    @GET("fetch_favorited_items.php")
    Call<List<FoodItem>> getFavoritedFoodItems();
    @POST("add_food_item.php")
    Call<Void> addFoodItem(@Body FoodItem foodItem);

    @Multipart
    @POST("upload_image.php")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part file);

    @PUT("update_food_rate.php")
    Call<Void> updateFoodRate(@Body FoodItem foodItem);

    @POST("registerWithImage.php")
    Call<Void> registerWithImage(@Body User user);

    @DELETE("delete_food.php")
    Call<Void> deleteFood(@Path("food_id") int food_id);
}
