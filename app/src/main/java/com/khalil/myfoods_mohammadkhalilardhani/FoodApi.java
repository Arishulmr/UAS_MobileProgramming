package com.khalil.myfoods_mohammadkhalilardhani;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.util.List;

public interface FoodApi {
    // Fetch all food items
    @GET("fetch_food_items.php")
    Call<List<FoodItem>> getFoodItems();

    // Method for adding food item with image
    @Multipart
    @POST("add_food_item_with_image.php")
    Call<Void> addFoodItemWithImage(
            @Part("foodData") RequestBody foodData,  // JSON food data
            @Part MultipartBody.Part image           // Image part
    );


    // Method for adding food item without image (just the food data)
    @POST("add_food_item.php")
    Call<Void> addFoodItem(@Body FoodItem foodItem); // Send the food item as JSON body
}
