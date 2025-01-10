package com.khalil.myfoods_mohammadkhalilardhani;

import com.khalil.myfoods_mohammadkhalilardhani.ImageResponse;
import com.khalil.myfoods_mohammadkhalilardhani.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserApi {

    @Multipart
    @POST("upload_image.php")
    Call<ImageResponse> uploadProfileImage(@Part MultipartBody.Part file);

    @POST("update_user_data.php")
    Call<Void> updateUserProfile(@Body User user);
}
