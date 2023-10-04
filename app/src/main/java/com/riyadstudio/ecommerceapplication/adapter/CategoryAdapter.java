package com.riyadstudio.ecommerceapplication.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder> {

    private onItemClickListener clickListener;
    private onItemLongClickListener longClickListener;
    private List<Category> categoryList = new ArrayList<>();

    public CategoryAdapter() {
    }

    @NonNull
    @Override
    public CategoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.signle_category_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterViewHolder holder, int position) {
        System.out.println("Hello "+categoryList);
        Category cat = categoryList.get(position);

        if(cat.getTitle()!=null){
            holder.categoryTitle.setText(cat.getTitle());
        }
        if(cat.getImgUrl()!=null){
            Glide.with(holder.itemView.getContext()).load(cat.getImgUrl()).override(200,200).into(holder.categoryImage);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public Category getCategoryAt(int position) {
        return categoryList.get(position);
    }
    public void setCategoryList(List<Category> categoryList){
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public class CategoryAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryTitle;

        public CategoryAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.SingleCategoryImage);
            categoryTitle = itemView.findViewById(R.id.SingleCategoryTitle);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(clickListener!=null && position!=RecyclerView.NO_POSITION){
                        clickListener.onItemClick(categoryList.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(longClickListener!=null && position!=RecyclerView.NO_POSITION){
                        longClickListener.onItemLongClick(categoryList.get(position));
                    }
                    return true;
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(Category singel_category);
    }

    public interface onItemLongClickListener {
        void onItemLongClick(Category singel_category);
    }

    public void setOnItemClickListener(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemLongClickListener(onItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

}
