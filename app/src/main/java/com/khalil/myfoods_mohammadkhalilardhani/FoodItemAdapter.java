package com.khalil.myfoods_mohammadkhalilardhani;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(FoodItem item);
    }
    private OnItemClickListener listener;
    private List<FoodItem> foodList;
    private Context context;


    public FoodItemAdapter(List<FoodItem> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);

    }


    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        FoodItem foodItem = foodList.get(position);

        holder.foodName.setText(foodItem.getFood_name());
        holder.foodPrice.setText(String.valueOf(foodItem.getFood_price()));
        holder.foodCategory.setText(String.valueOf(foodItem.getFood_category()));
        holder.foodWeight.setText(String.valueOf(foodItem.getFood_weight()));
        holder.foodQuantity.setText(String.valueOf(foodItem.getFood_quantity()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(foodItem));

        Glide.with(holder.itemView.getContext())
                .load(foodItem.getFood_image())
                .placeholder(R.drawable.burger)
                .circleCrop()
                .into(holder.foodImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(foodItem);
            }
        });


        if (foodItem.isFood_rate() == 1) {
            holder.likeButton.setTextColor(Color.YELLOW);
        } else {
            holder.likeButton.setTextColor(Color.GRAY);
        }

        holder.likeButton.setOnClickListener(v -> {
            int newRate = foodItem.isFood_rate() == 0 ? 1 : 0;
            foodItem.setFood_rate(newRate);

            updateFoodRate(foodItem);

            holder.likeButton.setTextColor(newRate == 1 ? Color.YELLOW : Color.GRAY);
        });

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAbsoluteAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteFoodItem(foodItem);
                foodList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
        });
    }

    private void updateFoodRate(FoodItem foodItem) {
        String url = "http://192.168.1.12:8080/myFoods_backend/" + "update_food_rate.php";

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("food_id", foodItem.getFood_id());
            jsonParams.put("food_rate", foodItem.isFood_rate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                response -> {
                    try {
                        Log.d("FoodItemAdapter", "Response: " + response.toString());
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Log.d("FoodItemAdapter", "Food rate updated successfully.");
                        } else {
                            Log.d("FoodItemAdapter", "Failed to update food rate.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }




    private void deleteFoodItem(FoodItem foodItem) {
        String url = "http://192.168.1.12:8080/myFoods_backend/"+"delete_food.php";

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("food_id", foodItem.getFood_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                response -> {
                    try {
                        Log.d("FoodItemAdapter", "Response: " + response.toString());
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Log.d("FoodItemAdapter", "Food deleted successfully.");
                        } else {
                            Log.d("FoodItemAdapter", "Failed to delete food.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, foodCategory, foodWeight, foodQuantity;
        ImageView foodImage;
        Button likeButton, deleteButton;

        public FoodViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodCategory = itemView.findViewById(R.id.foodCategory);
            foodWeight = itemView.findViewById(R.id.foodWeight);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            foodImage = itemView.findViewById(R.id.foodImage);
            likeButton = itemView.findViewById(R.id.likeButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


