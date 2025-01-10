package com.khalil.myfoods_mohammadkhalilardhani;

public class User {
    private String user_name;
    private String user_id;
    private String user_email;
    private String user_image;

    public User(String user_id, String user_name, String user_email, String user_image){
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_image = user_image;
    }

    public String getUsername() {
        return user_name;
    }

    public String getUserId() {
        return user_id;
    }

    public String getEmail() {
        return user_email;
    }

    public String getImage() {
        return "http://192.168.1.12:8080/myFoods_backend/uploads/" + user_image;
    }

    public void setUsername(String user_name) {
        this.user_name = user_name;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setEmail(String user_email) {
        this.user_email = user_email;
    }

    public void setImage(String image) {
        this.user_image = image;
    }
}
