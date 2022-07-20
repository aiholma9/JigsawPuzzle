package com.abdul.jigsawpuzzle.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdul.jigsawpuzzle.Interface.ItemClickListener;
import com.abdul.jigsawpuzzle.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView category_name;
    public ImageView puzzle;

    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /*
    Category name
     */
    public CategoryViewHolder(View itemView) {
        super(itemView);
        puzzle = (ImageView) itemView.findViewById(R.id.image);
        category_name = (TextView) itemView.findViewById(R.id.name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
