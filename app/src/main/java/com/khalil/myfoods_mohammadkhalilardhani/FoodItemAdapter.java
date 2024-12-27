package com.khalil.myfoods_mohammadkhalilardhani;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> foodList;

    // Constructor for the adapter, passing the food items list
    public FoodItemAdapter(List<FoodItem> foodList) {
        this.foodList = foodList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        // Get the food item at the given position
        FoodItem foodItem = foodList.get(position);

        // Bind data to the views in the food item layout
        holder.foodName.setText(foodItem.getFood_name());
        holder.foodPrice.setText(String.valueOf(foodItem.getFood_price()));
        holder.foodCategory.setText(String.valueOf(foodItem.getFood_category()));
        holder.foodWeight.setText(String.valueOf(foodItem.getFood_weight()));
        holder.foodQuantity.setText(String.valueOf(foodItem.getFood_quantity()));
        // You can add code to display the image, ratings, etc.
        // holder.foodImage.setImageResource(foodItem.getFoodImageResourceId());
    }

    @Override
    public int getItemCount() {
        // Return the size of the food list
        return foodList.size();
    }

    // ViewHolder class to hold the individual views
    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, foodPrice, foodCategory, foodWeight, foodQuantity;
        ImageView foodImage;
        Button likeButton;

        public FoodViewHolder(View itemView) {
            super(itemView);

            // Initialize the views
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodCategory = itemView.findViewById(R.id.foodCategory);
            foodWeight = itemView.findViewById(R.id.foodWeight);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            foodImage = itemView.findViewById(R.id.foodImage);
            likeButton = itemView.findViewById(R.id.likeButton);
        }
    }
}
