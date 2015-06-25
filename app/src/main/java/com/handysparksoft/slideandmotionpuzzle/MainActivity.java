package com.handysparksoft.slideandmotionpuzzle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Toast;

/*
    TODO:
	1 OK - Desordenacion correcta
	3 OK - Fluidez al mover --> glue effect --> metodos con dto
	5 OK - Mostrar ultima celda al solucionar
	9 - Mover con toques (acelerometro) o con inclinación
	12 - Mejorar el padding
	2 - Tiempo y puntuación
	4 - Ayuda / Acerca de...	
	6 - Tema full screen niño / adultos
	7 - upload foto
	8 - Seleccionar nivel
	10 - Transformación de foto segun ancho y alto
	11 - Puntuación como en Senku	
	13 - Ver puzzle resuelto
	14 - Votar
	15 - Share
	16 - Logo inicio
	17 - Icono
	18 - Modo juego contra-reloj
	19 - Temas: Frozen, Disney, Cars, Bob Esponja, Pepa Pig, Sports, Paisajes, Monumentos, 
	20 - Retar
	21 OK - setTag/getTag for buttons
	
	2X - Preferencias:
		 + 3x3, 4x4, 5x5
		 + Mostrar numeros
		 + Sonido
		 + Vibracion
		 + Nivel
	
	
*/
public class MainActivity extends ActionBarActivity implements SlidingPuzzleListener {

    private boolean initialized = false;
    private SlidingPuzzleGame slidingPuzzleGame;
    private int cols;
    private int rows;

    public static int PADDING = 4;
    private View.OnClickListener stickOnClickListener;


    private View.OnTouchListener stickOnTouchListener;
    private Button holeButton;
    private Drawable holeButtonBackground;

    private Vibrator vibrator;

    private boolean mustVibrate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(getString(R.string.broadcast_action_init))) {
                forceInit();
            }

            if (intent.getAction().equals(getString(R.string.broadcast_action_numbers))) {
                setNumbersVisible(getPreferenceNumbers());
            }
        }
    };

    private void forceInit() {
        setContentView(R.layout.activity_main);
        stickOnTouchListener = null;
        slidingPuzzleGame = null;
        initialized = false;
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(new RestartGameReceiver(this), new IntentFilter(getString(R.string.broadcast_action_init)));
        registerReceiver(br, new IntentFilter(getString(R.string.broadcast_action_init)));
        registerReceiver(br, new IntentFilter(getString(R.string.broadcast_action_numbers)));
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this add items to the action bar if it is present.
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_start) {
            actionStart();
        }

        return super.onOptionsItemSelected(item);
    }



    public void init() {

        //Init game
        int selectedDimension = getPreferenceDimension();
        slidingPuzzleGame = new SlidingPuzzleGame(selectedDimension, selectedDimension);
        cols = slidingPuzzleGame.getCols();
        rows = slidingPuzzleGame.getRows();

        stickOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(v);
            }
        };


        slidingPuzzleGame.registerListener(this);

        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        absoluteLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!initialized) {
                    initialized = true;
                    paintButtons();

                    //Numbers preference
                    setNumbersVisible(getPreferenceNumbers());
                }
            }
        });

        //Vibrate preference
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mustVibrate = getPreferenceVibrate();


    }

    private void actionStart() {
        slidingPuzzleGame.setLevel(getPreferenceLevel());
        hideLastButton();
        slidingPuzzleGame.start();
        repaintGame();
    }

    private void paintButtons() {

        //Drawable drawable = getDrawable(R.drawable.climbing);
        //BitmapDrawable drawable = new BitmapDrawable(getResources(), R.drawable.climbing);
        Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.climbing);

        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int wLayout = mainLayout.getMeasuredWidth() - ((cols - 1) * PADDING);
        int hLayout = mainLayout.getMeasuredHeight() - ((rows - 1) * PADDING);
        int wButton = getStickWidth();
        int hButton = getStickHeight();
        PADDING = 0;
        int counter = 1;
        int dimension = getPreferenceDimension();

        bitMap = Bitmap.createScaledBitmap(bitMap, wLayout, hLayout, true);

        //Add buttons (Sticks)

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {


                final Button button = new Button(this);
                int x = (i * wButton);
                int y = (j * hButton);
                AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(wButton, hButton, x, y);
                button.setLayoutParams(lp);
                button.setTag(String.valueOf(counter++));
                button.setTextSize(35 - (dimension * 3));
                button.setTextColor(Color.LTGRAY);

                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ComicRelief.ttf");
                button.setTypeface(font);


                Bitmap square = Bitmap.createBitmap(bitMap, x, y, wButton, hButton);
                Drawable buttonBackground = new BitmapDrawable(getResources(), square).getCurrent();
                button.setBackgroundDrawable(buttonBackground);



                mainLayout.addView(button);

                if (i == cols - 1 && j == rows - 1) {
                    button.setVisibility(View.INVISIBLE);
                    holeButtonBackground = buttonBackground;
                    holeButton = button;

                } else {

                    //Listeners
                    if (stickOnTouchListener == null) {
                        int[] baseLocation = new int[2];
                        findViewById(R.id.layoutMain).getLocationOnScreen(baseLocation);

                        stickOnTouchListener = new StickTouchListener(this, slidingPuzzleGame, baseLocation[0], baseLocation[1]);
                    }

                    button.setOnClickListener(stickOnClickListener);
                    button.setOnTouchListener(stickOnTouchListener);
                }
            }
        }
    }

    private void buttonPressed(View v) {
        Button button = (Button) v;

        int idButton = Integer.valueOf(((Button) v).getTag().toString());
        slidingPuzzleGame.play(idButton);


    }

    private void setNumbersVisible(boolean value) {
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {

                int idButton = slidingPuzzleGame.getCell(i, j);
                Button button = findButtonByTag(idButton);
                if (button != null) {
                    if (value) {
                        button.setText(String.valueOf(button.getTag()));
                    } else {
                        button.setText("");
                    }
                }
            }
        }
    }

    private void repaintGame() {

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {

                int idButton = slidingPuzzleGame.getCell(i, j);
                Button button = findButtonByTag(idButton);
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
        int wLayout = mainLayout.getMeasuredWidth() - ((cols - 1) * PADDING);

        int wButton = wLayout / cols;

        return wButton;
    }

    private int getStickHeight() {
        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int hLayout = mainLayout.getMeasuredHeight() - ((rows - 1) * PADDING);
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
                if (idButton == Integer.valueOf(String.valueOf(currentButton.getTag()))) {
                    result = (Button) child;
                    break;
                }
            }
        }
        return result;
    }

    private Button findButtonByTag(int idButton) {
        Button result = null;
        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        for (int k = 0; k < absoluteLayout.getChildCount(); k++) {
            View child = absoluteLayout.getChildAt(k);
            if (child instanceof Button) {
                Button currentButton = (Button) child;
                if (idButton == Integer.valueOf(String.valueOf(currentButton.getTag()))) {
                    result = (Button) child;
                    break;
                }
            }
        }
        return result;
    }

    private void paintLastButton() {
        holeButton.setBackgroundDrawable(holeButtonBackground);
        paintViewAt(holeButton,cols -1, rows-1);
        holeButton.setVisibility(View.VISIBLE);
    }

    private void hideLastButton() {
        holeButton.setBackgroundDrawable(null);
        holeButton.setVisibility(View.INVISIBLE);
    }

    //Methods for get preferences
    private int getPreferenceDimension() {
        int result = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_list_dimension), "3"));
        result = result < 3 ? 3 : result;
        return result;
    };

    private int getPreferenceLevel() {
        int result = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.pref_level), "1"));
        return result;
    };

    private boolean getPreferenceVibrate() {
        boolean result = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_vibrate), true);
        return result;
    };

    private boolean getPreferenceSound() {
        boolean result = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_sound), true);
        return result;
    };

    private boolean getPreferenceNumbers() {
        boolean result = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_numbers), true);
        return result;
    };



    //Listeners
    @Override
    public void onPlayListener() {
        if (mustVibrate) {
            vibrator.vibrate(15);
        }
        repaintGame();

        if (slidingPuzzleGame.isSolved()) {
            //Show last button
            paintLastButton();

            repaintGame();

            Toast.makeText(this, "The game is finished", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRepaintListener() {
        repaintGame();
    }
}

