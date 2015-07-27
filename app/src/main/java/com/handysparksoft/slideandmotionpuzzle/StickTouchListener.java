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

    private Long lastDownEvent = null;

    private static int PROXIMITY_THRESHOLD = 25;
    private static int TOUCH_CLICK_THRESHOLD = 350;

    boolean isMoving = false;

    public StickTouchListener(Context context, SlidingPuzzleGame slidingPuzzleGame) {
        this.context = context;
        this.slidingPuzzleGame = slidingPuzzleGame;
    }

    public StickTouchListener(Context context, SlidingPuzzleGame slidingPuzzleGame, int leftMargin, int topMargin) {
        this(context, slidingPuzzleGame);

        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float currX = 0;
        float currY = 0;
        int action = event.getAction();
        Button button = (Button) v;
        int idButton = Integer.valueOf(button.getTag().toString());

        if (!slidingPuzzleGame.isFinished()) {
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    lastDownEvent = System.currentTimeMillis();
                    isMoving = false;

                    mPrevX = event.getRawX();
                    mPrevY = event.getRawY();
                    slidingPuzzleGame.fireRepaintEvent(); //Esta bien esta linea??


                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    isMoving = true;

                    currX = event.getRawX();
                    currY = event.getRawY(); //Restamos top

                    if (slidingPuzzleGame.canMoveInXAxis(idButton)) {
                        int calcX = (int) (currX - (v.getWidth() / 2));
                        changeXIfInsideBounds(v, calcX);
                        //if (insideXBounds(v, calcX)) {
                        //	v.setX(calcX);
                        //}
                    } else if (slidingPuzzleGame.canMoveInYAxis(idButton)) {
                        int calcY = (int) (currY - topMargin - (v.getHeight() / 2));
                        changeYIfInsideBounds(v, calcY);

                        //if (insideYBounds(v, calcY)) {
                        //	v.setY(calcY);
                        //}
                    }

                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                    //slidingPuzzleGame.firePlayEvent();
                    break;

                case MotionEvent.ACTION_UP:
                    long time = System.currentTimeMillis();
                    if (!isMoving && time - lastDownEvent < TOUCH_CLICK_THRESHOLD) {
                        v.performClick();
                    } else {

                        currX = event.getRawX();
                        currY = event.getRawY();
                        boolean leftToRight = currX > mPrevX;
                        boolean topToBottom = currY > mPrevY;
                        if (Math.abs(currX - mPrevX) > (v.getWidth() / 2)) {
                            if (leftToRight && slidingPuzzleGame.getSideCell(idButton, SlidingPuzzleGame.CellSideType.RIGHT) == SlidingPuzzleGame.getHoleTag()) {
                                slidingPuzzleGame.play(idButton);
                            } else if (!leftToRight && slidingPuzzleGame.getSideCell(idButton, SlidingPuzzleGame.CellSideType.LEFT) == SlidingPuzzleGame.getHoleTag()) {
                                slidingPuzzleGame.play(idButton);
                            } else {
                                slidingPuzzleGame.fireRepaintEvent();
                            }
                        } else if (Math.abs(mPrevY - currY) > (v.getHeight() / 2)) {
                            if (topToBottom && slidingPuzzleGame.getSideCell(idButton, SlidingPuzzleGame.CellSideType.BOTTOM) == SlidingPuzzleGame.getHoleTag()) {
                                slidingPuzzleGame.play(idButton);
                            } else if (!topToBottom && slidingPuzzleGame.getSideCell(idButton, SlidingPuzzleGame.CellSideType.TOP) == SlidingPuzzleGame.getHoleTag()) {
                                slidingPuzzleGame.play(idButton);
                            } else {
                                slidingPuzzleGame.fireRepaintEvent();
                            }
                        } else {
                            slidingPuzzleGame.fireRepaintEvent();
                        }
                    }

                    break;
            }
        }

        Log.d(LOG_TAG, "TouchEvent: " + action);
        return true;
    }

    public boolean changeXIfInsideBounds(View v, int x) {
        boolean result = false;
        Button button = (Button) v;
        int idButton = Integer.valueOf(button.getTag().toString());

        int[] coordsById = slidingPuzzleGame.getCoordsById(idButton);
        int[] coordsHole = slidingPuzzleGame.getHoleCoords();
        int buttonXCoord = coordsById[0];
        int holeXCoord = coordsHole[0];
        int startX = buttonXCoord * v.getWidth();
        int endX = startX + v.getWidth();

        if (buttonXCoord < holeXCoord) {
            if (x > startX && x < (endX)) {
                result = true;
            }
        } else if (buttonXCoord > holeXCoord) {
            endX = startX - v.getWidth();
            if (x < startX && x > endX) {
                result = true;
            }
        }

        if (result) {
            //Adjust effect
            if (Math.abs(x - startX) < PROXIMITY_THRESHOLD) {
                x = startX;
            } else if (Math.abs(x - (endX)) < PROXIMITY_THRESHOLD) {
                x = endX;
            }
            v.setX(x);
        }

        return result;
    }

    public boolean changeYIfInsideBounds(View v, int y) {
        boolean result = false;
        Button button = (Button) v;
        int idButton = Integer.valueOf(button.getTag().toString());

        int[] coordsById = slidingPuzzleGame.getCoordsById(idButton);
        int[] coordsHole = slidingPuzzleGame.getHoleCoords();
        int buttonYCoord = coordsById[1];
        int holeYCoord = coordsHole[1];
        int startY = buttonYCoord * v.getHeight();
        int endY = startY + v.getHeight();


        if (buttonYCoord < holeYCoord) {
            if (y > startY && y < (endY)) {
                result = true;
            }
        } else if (buttonYCoord > holeYCoord) {
            endY = startY - v.getHeight();
            if (y < startY && y > endY) {
                result = true;
            }
        }

        if (result) {
            //Adjust effect
            if (Math.abs(y - startY) < PROXIMITY_THRESHOLD) {
                y = startY;
            } else if (Math.abs(y - (endY)) < PROXIMITY_THRESHOLD) {
                y = endY;
            }
            v.setY(y);
        }

        return result;
    }
}
