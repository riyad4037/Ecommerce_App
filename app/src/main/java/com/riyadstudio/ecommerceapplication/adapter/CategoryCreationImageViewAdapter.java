package com.riyadstudio.ecommerceapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riyadstudio.ecommerceapplication.R;

public class CategoryCreationImageViewAdapter extends RecyclerView.Adapter<CategoryCreationImageViewAdapter.CategoryCreationImageViewHolderClass> {
    @NonNull
    @Override
    public CategoryCreationImageViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryCreationImageViewHolderClass(LinearLayout.inflate(parent.getContext(), R.layout.single_image_design,null), LinearLayout.HORIZONTAL, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCreationImageViewHolderClass holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CategoryCreationImageViewHolderClass extends RecyclerView.ViewHolder {

        ImageView itemImage;
        ImageView removeImage;
        public CategoryCreationImageViewHolderClass(@NonNull View itemView, int horizontal, ViewGroup parent) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.product_Image);
            removeImage = itemView.findViewById(R.id.Close_Image_Btn);
        }
    }
}
