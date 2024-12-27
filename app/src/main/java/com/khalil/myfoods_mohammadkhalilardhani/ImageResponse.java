package com.khalil.myfoods_mohammadkhalilardhani;

import com.google.gson.annotations.SerializedName;

public class ImageResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("imageUrl")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

