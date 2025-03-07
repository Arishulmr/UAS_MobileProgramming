package com.khalil.myfoods_mohammadkhalilardhani;

public class FoodItem {

    private int food_id;
    private String food_name;
    private String food_category;
    private int food_weight;
    private String food_type;
    private String food_desc;
    private String food_image;
    private int food_quantity;
    private int food_price;
    private int food_rate;

    public FoodItem(String food_name, String food_category, String food_type,
                    int food_weight, int food_price, int food_quantity, String food_desc,
                    String food_image) {
        this.food_name = food_name;
        this.food_image = food_image;
        this.food_type = food_type;
        this.food_desc = food_desc;
        this.food_category = food_category;
        this.food_weight = food_weight;
        this.food_quantity = food_quantity;
        this.food_price = food_price;
    }




    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {

        this.food_name = food_name;
    }

    public String getFood_category() {
        return food_category;
    }

    public void setFood_category(String food_category) {
        this.food_category = food_category;
    }

    public int getFood_weight() {
        return food_weight;
    }

    public void setFood_weight(Integer food_weight) {
        this.food_weight = food_weight;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getFood_desc() {
        return food_desc;
    }

    public void setFood_desc(String food_desc) {
        this.food_desc = food_desc;
    }

    public String getFood_image() {
        return  "http://192.168.1.12:8080/myFoods_backend/uploads/" + food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public int getFood_quantity() {
        return food_quantity;
    }

    public void setFood_quantity(int food_quantity) {
        this.food_quantity = food_quantity;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(Integer food_price) {
        this.food_price = food_price;
    }

    public int isFood_rate() {
        return food_rate;
    }

    public void setFood_rate(int food_rate) {
        this.food_rate = food_rate;
    }
}
