package com.khalil.myfoods_mohammadkhalilardhani;

public class User {
    private String username;
    private String password;
    private String email;
    private String image;

    public User(String username, String password, String email, String image){
        this.username = username;
        this.password = password;
        this.email = email;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
