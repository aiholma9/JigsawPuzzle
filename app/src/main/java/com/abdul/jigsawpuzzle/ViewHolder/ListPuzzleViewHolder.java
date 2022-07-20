package com.abdul.jigsawpuzzle.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.abdul.jigsawpuzzle.Interface.ItemClickListener;
import com.abdul.jigsawpuzzle.R;

public class ListPuzzleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ItemClickListener itemClickListener;
    public ImageView jigsawPuzzle;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListPuzzleViewHolder(View itemView) {
        super(itemView);
        jigsawPuzzle = (ImageView)itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
