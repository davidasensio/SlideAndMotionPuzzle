package com.handysparksoft.slideandmotionpuzzle;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by davasens on 6/15/2015.
 */
public class StickTouchListener implements View.OnTouchListener {
    private Context context;

    private float mPrevX;
    private float mPrevY;

    public StickTouchListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float currX,currY;
        int action = event.getAction();
        switch (action ) {
            case MotionEvent.ACTION_DOWN: {

                mPrevX = event.getX();
                mPrevY = event.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {

                currX = event.getRawX();
                currY = event.getRawY();


                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
                marginParams.setMargins((int)(currX - mPrevX), (int)(currY - mPrevY),0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                v.setLayoutParams(layoutParams);


                break;
            }



            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP:

                break;
        }

        return true;
    }
}
