package com.khalil.myfoods_mohammadkhalilardhani;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface FoodApi {
    @GET("fetch_food_items.php")
    Call<List<FoodItem>> getFoodItems();
    @GET("search_food.php")
    Call<List<FoodItem>> searchFood(@Query("input") String input);
    @POST("fetch_filtered_items.php")
    @Headers("Content-Type: application/json")
    Call<List<FoodItem>> getFilteredItems(@Body Map<String, String> body);

    @POST("fetch_sorted_items.php")
    @Headers("Content-Type: application/json")
    Call<List<FoodItem>> getSortedItems(@Body Map<String, String> body);


    @POST("add_food_item.php")
    Call<Void> addFoodItem(@Body FoodItem foodItem);

    @Multipart
    @POST("upload_image.php")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part file);


    @POST("registerWithImage.php")
    Call<Void> registerWithImage(@Body User user);

}
