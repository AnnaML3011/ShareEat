package com.example.shareeat.utils;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shareeat.R;
import com.example.shareeat.objects.Recipe;

import java.util.List;

public class Adapter_WishList extends RecyclerView.Adapter<Adapter_WishList.MyViewHolder>{
    private List<Recipe> recipes_WL;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;

    public Adapter_WishList(Context context, List<Recipe> recipes_WL){
        this.mInflater = LayoutInflater.from(context);
        this.recipes_WL = recipes_WL;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.one_wishlist_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_WishList.MyViewHolder holder, int position) {
        Recipe c = recipes_WL.get(position);
        holder.recipe_title_WL_LBL.setText("" + c.getRecipeName());
        Glide
                .with(mInflater.getContext())
                .load(c.getRecipeImage())
                .centerCrop()
                .into(holder.recipe_image_WL_IMG);

        holder.save_to_WL_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onWishListClicked(v, c);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes_WL.size();
    }

    Recipe getItem(int id){
        return recipes_WL.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(MyItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
        void onWishListClicked(View view, Recipe recipe);
    }


    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recipe_image_WL_IMG;
        TextView recipe_title_WL_LBL;
        ImageButton save_to_WL_BTN;

        MyViewHolder(View itemView) {
            super(itemView);
            recipe_image_WL_IMG = itemView.findViewById(R.id.recipe_image_WL_IMG);
            recipe_title_WL_LBL = itemView.findViewById(R.id.recipe_title_WL_LBL);
            save_to_WL_BTN = itemView.findViewById(R.id.save_to_WL_BTN);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }
    }
}

