package com.abdul.jigsawpuzzle.Model;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

public class PuzzlePiece extends AppCompatImageView{
    public int xCoordinate, yCoordinate, pieceWidth,pieceHeight;
    public boolean canMove = true;

    public PuzzlePiece(Context context){
        super(context);
    }
}
