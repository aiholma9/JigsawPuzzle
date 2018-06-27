package com.abdul.jigsawpuzzle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class TouchFunctionality implements View.OnTouchListener{
    float X =0;
    float Y =0;

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        RelativeLayout.LayoutParams relativeLayout = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return true;
    }
}
