package com.handysparksoft.slideandmotionpuzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private boolean initialized = false;
    private SlidingPuzzleGame slidingPuzzleGame;
    private int cols;
    private int rows;
    private int padding = 4;

    private View.OnClickListener stickOnClickListener;
    private View.OnTouchListener stickOnTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_start) {
            slidingPuzzleGame.start();
            repaintGame();
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {

        //Init game
        slidingPuzzleGame = new SlidingPuzzleGame();
        cols = slidingPuzzleGame.getCols();
        rows = slidingPuzzleGame.getRows();

        stickOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(v);
            }
        };

        stickOnTouchListener = new StickTouchListener(this);

        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        absoluteLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!initialized) {
                    initialized = true;
                    paintButtons();
                }
            }
        });

    }

    private void paintButtons() {

        //Drawable drawable = getDrawable(R.drawable.climbing);
        //BitmapDrawable drawable = new BitmapDrawable(getResources(), R.drawable.climbing);
        Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.climbing);

        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int wLayout = mainLayout.getMeasuredWidth() - ((cols - 1) * padding);
        int hLayout = mainLayout.getMeasuredHeight() - ((rows - 1) * padding);
        int wButton =getStickWidth();
        int hButton = getStickHeight();
        int counter = 1;

        bitMap = Bitmap.createScaledBitmap(bitMap, wLayout, hLayout, true);

        //Add buttons (Sticks)
        padding = 0;

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {

                if (i != cols - 1 || j != rows - 1) {
                    final Button button = new Button(this);
                    int x = (i * wButton) + padding;
                    int y = (j * hButton) + padding;
                    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(wButton, hButton, x, y);
                    button.setLayoutParams(lp);
                    button.setText(String.valueOf(counter++));

                    Bitmap square = Bitmap.createBitmap(bitMap, x, y, wButton - padding, hButton - padding);
                    button.setBackgroundDrawable(new BitmapDrawable(getResources(), square).getCurrent());

                    //Listeners
                    button.setOnClickListener(stickOnClickListener);
                    button.setOnTouchListener(stickOnTouchListener);

                    mainLayout.addView(button);
                }
            }
        }
    }

    private void buttonPressed(View v) {
        Button button = (Button) v;

        int idButton = Integer.valueOf(((Button) v).getText().toString());
        slidingPuzzleGame.play(idButton);

        repaintGame();

        if (slidingPuzzleGame.isSolved()) {
            Toast.makeText(this, "The game is finished", Toast.LENGTH_SHORT).show();
        }
    }

    private void repaintGame() {

        for (int j = 0; j < rows; j++) {
        for (int i = 0; i < cols; i++) {

                int idButton = slidingPuzzleGame.getCell(i, j);
                Button button = findButtonById(idButton);
                if (button != null) {
                    paintViewAt(button, i, j);
                }
            }
        }
    }

    private void paintViewAt(View view, int i, int j) {
        view.setX(getStickWidth() * i);
        view.setY(getStickHeight() * j);
    }

    private int getStickWidth() {
        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int wLayout = mainLayout.getMeasuredWidth() - ((cols - 1) * padding);

        int wButton = wLayout / cols;

        return wButton;
    }

    private int getStickHeight() {
        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int hLayout = mainLayout.getMeasuredHeight() - ((rows - 1) * padding);
        int hButton = hLayout / rows;

        return hButton;
    }

    private Button findButtonById(int idButton) {
        Button result = null;
        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        for (int k = 0; k < absoluteLayout.getChildCount(); k++) {
            View child = absoluteLayout.getChildAt(k);
            if (child instanceof Button) {
                Button currentButton = (Button) child;
                if (idButton == Integer.valueOf(currentButton.getText().toString())) {
                    result = (Button) child;
                    break;
                }
            }
        }
        return result;
    }


}

