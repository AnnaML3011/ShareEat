package com.example.shareeat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class Adapter_Recipe extends RecyclerView.Adapter<Adapter_Recipe.MyViewHolder>{
    private List<Recipe> recipes;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;

    Adapter_Recipe(Context context,  List<Recipe> recipes){
        this.mInflater = LayoutInflater.from(context);
        this.recipes = recipes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.one_reciepe_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Recipe.MyViewHolder holder, int position) {
        Log.d("pttt", "Position = " + position);
        Recipe r = recipes.get(position);
        holder.name_RECIPE_LBL.setText("" + r.getRecipeName());
        holder.category_RECIPE_LBL.setText("" + r.getCategory());
        holder.prep_TIME_LBL.setText(r.getPreparationTime());
        holder.description__RECIPE_LBL.setText(r.getRecipeDescription());

        Glide
                .with(mInflater.getContext())
                .load(r.getRecipeImage())
                .centerCrop()
                .into(holder.recipe_img_IMG);


        holder.main_BTN_readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onReadMoreClicked(v, r);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    Recipe getItem(int id){
        return recipes.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(MyItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
        void onReadMoreClicked(View view, Recipe recipe);
    }

    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recipe_img_IMG;
        TextView name_RECIPE_LBL;
        TextView category_RECIPE_LBL;
        TextView prep_TIME_LBL;
        TextView description__RECIPE_LBL;
        MaterialButton main_BTN_readMore;
        AppManager appManager = new AppManager();

        MyViewHolder(View itemView) {
            super(itemView);
//            appManager.findViewsAdpterRecipe(itemView);
            recipe_img_IMG = itemView.findViewById(R.id.recipe_img_IMG);
            name_RECIPE_LBL = itemView.findViewById(R.id.name_RECIPE_LBL);
            category_RECIPE_LBL = itemView.findViewById(R.id.category_RECIPE_LBL);
            prep_TIME_LBL = itemView.findViewById(R.id.prep_TIME_LBL);
            description__RECIPE_LBL = itemView.findViewById(R.id.description__RECIPE_LBL);
            main_BTN_readMore = itemView.findViewById(R.id.main_BTN_readMore);

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
