package com.abdul.jigsawpuzzle.Touch;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.abdul.jigsawpuzzle.Activity.GameActivity;
import com.abdul.jigsawpuzzle.Model.PuzzlePiece;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TouchFunctionality implements View.OnTouchListener{

    private float X;
    private float Y;
    private GameActivity gameActivity;

    private boolean longActive = true;

    public TouchFunctionality(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int xDiff, yDiff;
        final double tolerance = sqrt(pow(view.getWidth(),2) + pow(view.getHeight(), 2))/10;

        PuzzlePiece puzzlePiece = (PuzzlePiece)view;
        if (!puzzlePiece.canMove)
            return true;

        RelativeLayout.LayoutParams relativeLayout = (RelativeLayout.LayoutParams) view.getLayoutParams();

        //drags a piece across the screen
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                X = event.getRawX() - relativeLayout.leftMargin;
                Y = event.getRawY()-relativeLayout.topMargin;
                puzzlePiece.bringToFront();
                break;

            case MotionEvent.ACTION_MOVE:
                relativeLayout.leftMargin = (int)(event.getRawX() -X);
                relativeLayout.topMargin = (int)(event.getRawY() -Y);
                view.setLayoutParams(relativeLayout);
                break;

            case MotionEvent.ACTION_UP:
                xDiff = abs(puzzlePiece.xCoordinate - relativeLayout.leftMargin);
                yDiff = abs(puzzlePiece.yCoordinate - relativeLayout.topMargin);
                if (xDiff <= tolerance && yDiff <= tolerance){
                    relativeLayout.leftMargin = puzzlePiece.xCoordinate;
                    relativeLayout.topMargin = puzzlePiece.yCoordinate;
                    puzzlePiece.setLayoutParams(relativeLayout);
                    puzzlePiece.canMove = false;
                    sendViewToBack(puzzlePiece);
                    gameActivity.checkGame();
                    longActive = false;
                }
                break;

        }
        return true;
    }

    private void sendViewToBack(final View view) {
        final ViewGroup viewGroup = (ViewGroup)view.getParent();
        if (null != viewGroup){
            viewGroup.removeView(view);
            viewGroup.addView(view, 0);
        }
    }
}
