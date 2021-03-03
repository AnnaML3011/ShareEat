package com.example.shareeat.utils;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shareeat.objects.Category;
import com.example.shareeat.R;
import java.util.List;



public class Adapter_Categories extends RecyclerView.Adapter<Adapter_Categories.MyViewHolder>{
    private List<Category> categories;
    private LayoutInflater mInflater;
    private MyItemClickListener mClickListener;

    public Adapter_Categories(Context context, List<Category> categories){
        this.mInflater = LayoutInflater.from(context);
        this.categories = categories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.one_category_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Categories.MyViewHolder holder, int position) {
        Category c = categories.get(position);
        holder.category_title_LBL.setText("" + c.getCategory_Name());
        Glide
                .with(mInflater.getContext())
                .load(c.getCategory_image())
                .centerCrop()
                .into(holder.catrgory_image_IMG);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    Category getItem(int id){
        return categories.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(MyItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView catrgory_image_IMG;
        TextView category_title_LBL;

        MyViewHolder(View itemView) {
            super(itemView);
            catrgory_image_IMG = itemView.findViewById(R.id.catrgory_image_IMG);
            category_title_LBL = itemView.findViewById(R.id.category_title_LBL);

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

