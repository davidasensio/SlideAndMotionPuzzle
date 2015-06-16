package com.handysparksoft.slideandmotionpuzzle;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by davasens on 6/15/2015.
 */
public class StickTouchListener implements View.OnTouchListener {
	private final static String LOG_TAG = StickTouchListener.class.getSimpleName();
	private Context context;
    private SlidingPuzzleGame slidingPuzzleGame;

    private float mPrevX;
    private float mPrevY;

	public int topMargin = 0;
	public int leftMargin = 0;

    public StickTouchListener(Context context, SlidingPuzzleGame slidingPuzzleGame) {
        this.context = context;
        this.slidingPuzzleGame = slidingPuzzleGame;
    }
	public StickTouchListener(Context context, SlidingPuzzleGame slidingPuzzleGame, int leftMargin, int topMargin) {
		this.context = context;
		this.slidingPuzzleGame = slidingPuzzleGame;
		this.leftMargin = leftMargin;
		this.topMargin = topMargin;
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float currX = 0;
		float currY = 0;
        int action = event.getAction();
		Button button = (Button) v;
		int idButton = Integer.valueOf(button.getText().toString());
		
        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                mPrevX = event.getRawX();
                mPrevY = event.getRawY();
				slidingPuzzleGame.fireRepaintEvent(); //Esta bien esta linea??
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {


                currX = event.getRawX();
                currY = event.getRawY(); //Restamos top

                if (slidingPuzzleGame.canMoveInXAxis(idButton)) {
					int calcX = (int) (currX - (v.getWidth() / 2));
					if (insideXBounds(v, calcX)) {
						v.setX(calcX);
					}
                }else if (slidingPuzzleGame.canMoveInYAxis(idButton)) {
					int calcY = (int) (currY - topMargin - (v.getHeight() / 2));
					if (insideYBounds(v, calcY)) {
						v.setY(calcY);
					}
                }



//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
//                marginParams.setMargins((int)(currX - mPrevX), (int)(currY - mPrevY),0, 0);
//                AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(marginParams);
//                v.setLayoutParams(layoutParams);

                break;
            }



            case MotionEvent.ACTION_CANCEL:
				//slidingPuzzleGame.firePlayEvent();
                break;

            case MotionEvent.ACTION_UP:
				//FIXME: Move to nearest place
				currX = event.getRawX();
				currY = event.getRawY();
				if (Math.abs(mPrevX - currX) > (v.getWidth() / 2) || Math.abs(mPrevY - currY) > (v.getHeight() / 2)) {
					slidingPuzzleGame.play(idButton);
					
				}else {
					slidingPuzzleGame.fireRepaintEvent();
				}

                break;
        }

		Log.d(LOG_TAG, "TouchEvent: " + action);
        return true;
    }
	
	public boolean insideXBounds(View v, int x) {
		boolean result = false;
		Button button = (Button) v;
		int idButton = Integer.valueOf(button.getText().toString());
		
		int[] coordsById = slidingPuzzleGame.getCoordsById(idButton);
		int[] coordsHole = slidingPuzzleGame.getHoleCoords();
		int buttonXCoord = coordsById[0];
		int holeXCoord = coordsHole[0];
		int startX = buttonXCoord * v.getWidth();

		if (buttonXCoord < holeXCoord) {
			if (x > startX && x < (startX + v.getWidth())) {
				result = true;
			}
		} else if (buttonXCoord > holeXCoord) {
			if (x < startX && x > (startX - v.getWidth())) {
				result = true;
			}
		}
		
		return result;
	}
	
	public boolean insideYBounds(View v, int y) {
		boolean result = false;
		Button button = (Button) v;
		int idButton = Integer.valueOf(button.getText().toString());
		
		int[] coordsById = slidingPuzzleGame.getCoordsById(idButton);
		int[] coordsHole = slidingPuzzleGame.getHoleCoords();
		int buttonYCoord = coordsById[1];
		int holeYCoord = coordsHole[1];
		int startY = buttonYCoord * v.getHeight();
		
		if (buttonYCoord < holeYCoord) {
			if (y > startY  && y < (startY + v.getHeight())) {
				result = true;
			}
		} else if (buttonYCoord > holeYCoord) {
			if ( y < startY  && y > (startY - v.getHeight())) {
				result = true;
			}
		}
		
		return result;
	}
}
