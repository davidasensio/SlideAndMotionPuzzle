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
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
    TODO:
	9 a  - Mover con (acelerometro) o con inclinación
	2 - Tiempo y puntuación
	4 - Ayuda / Acerca de...	
	6 - Tema full screen niño / adultos
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
	7 - Pick picture
	23 - Save last URI in preferences
	24 - Muestra una pista: Resaltar un cuadrado no ubicado y animarlo hacia su posición correcta
	
	1 OK - Desordenacion correcta
	3 OK - Fluidez al mover --> glue effect --> metodos con dto
	5 OK - Mostrar ultima celda al solucionar
	22 OK - Mantener orden y repintar al cambiar orientación
	9 OK - Mover con toques
	12 OK - Mejorar el padding
	7a OK - Action SEND picture
	8 OK - Seleccionar nivel
	21 OK - setTag/getTag for buttons
	
	2X - OK Preferencias:
		 + 3x3, 4x4, 5x5, custom
		 + Mostrar numeros
		 + Sonidos
		 + Vibracion
		 + Nivel
*/
public class MainActivity extends ActionBarActivity implements SlidingPuzzleListener {

    private boolean initialized = false;
    private SlidingPuzzleGame slidingPuzzleGame;

    public static int PADDING = 3;
    private View.OnClickListener stickOnClickListener;


    private View.OnTouchListener stickOnTouchListener;
    private Button holeButton;
    private Drawable holeButtonBackground;

    private Vibrator vibrator;

    private boolean mustVibrate = true;

    private Bundle lastSavedInstanceState = null;

    private Uri imageUri = null;

    //private AbsoluteLayout absoluteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        setContentView(R.layout.activity_main);

        if (action.equals(Intent.ACTION_SEND)) {
            handleSendImage(intent);
        }else if (action.equals(Intent.ACTION_MAIN)) {

        }

        if (savedInstanceState != null) {
            lastSavedInstanceState = savedInstanceState;
        }
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

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            //imageView.setImageURI(imageUri);

            //Bitmap myImg = BitmapFactory.decodeStream(new URL(imageUri.get).openStream());
            try {

                Bitmap myImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                //Matrix matrix = new Matrix();
                //matrix.postRotate(90);
                //Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(), matrix, true);

                imageView.setImageBitmap(myImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        //try {
        //    unregisterReceiver(br);
        //    unregisterReceiver(br);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("initialized", initialized);
        outState.putBoolean("finished", slidingPuzzleGame.isFinished());
        outState.putString("snapshot", slidingPuzzleGame.getSnapshot());
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

        if (id == R.id.action_show_image) {
            Intent intent = new Intent(this, ShowCurrentImage.class);
            intent.putExtra("current_image_uri", imageUri);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    public void init() {

        //Init game
        int selectedDimension = getPreferenceDimension();
        slidingPuzzleGame = new SlidingPuzzleGame(selectedDimension, selectedDimension);

        stickOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed(v);
            }
        };

        slidingPuzzleGame.registerListener(this);

        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }

            @Override
            public void onGlobalLayout() {
                if (!initialized) {
                    initialized = true;

                    if (lastSavedInstanceState != null) {
                        String snapshot = lastSavedInstanceState.getString("snapshot");
                        Boolean finished = lastSavedInstanceState.getBoolean("finished");
                        slidingPuzzleGame.setFromSnapshot(snapshot);
                        slidingPuzzleGame.setFinished(finished);
                                //hideLastButton();
                        //repaintGame();
                        //paintButtons();
                        initialized = lastSavedInstanceState.getBoolean("initialized");
                        lastSavedInstanceState = null;
                    }
                    paintButtons();

                    //Numbers preference
                    setNumbersVisible(getPreferenceNumbers());

                    repaintGame();
                    //hideLastButton();

                }
            }


        };

        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        absoluteLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        absoluteLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                repaintGame();
            }
        });


        //Vibrate preference
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        mustVibrate = getPreferenceVibrate();


    }

    private void actionStart() {
        //forceInit();

        slidingPuzzleGame.setLevel(getPreferenceLevel());
        hideLastButton();
        slidingPuzzleGame.start();
        //paintButtons();
        repaintGame();
        //slidingPuzzleGame.setFinished(false);

    }

    private void paintButtons() {

        //Drawable drawable = getDrawable(R.drawable.climbing);
        //BitmapDrawable drawable = new BitmapDrawable(getResources(), R.drawable.climbing);
        //Default Image
        Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.climbing);
        imageUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.climbing);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (imageView != null) {
            BitmapDrawable drawable = (BitmapDrawable) (imageView).getDrawable();
            if (drawable != null) {
                bitMap = drawable.getBitmap();
            }
        }

        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int wLayout = mainLayout.getMeasuredWidth() ;//- ((cols - 1) * PADDING);
        int hLayout = mainLayout.getMeasuredHeight();// - ((rows - 1) * PADDING);
        int wButton = getStickWidth();
        int hButton = getStickHeight();
        //PADDING = 0;
        int counter = 1;
        int dimension = getPreferenceDimension();

        bitMap = Bitmap.createScaledBitmap(bitMap, wLayout, hLayout, true);

        //Add buttons (Sticks)

        for (int j = 0; j < slidingPuzzleGame.getRows(); j++) {
            for (int i = 0; i < slidingPuzzleGame.getCols(); i++) {


                Button button = new Button(this);
                int x = (i * wButton);
                int y = (j * hButton);
                AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(wButton, hButton, x, y);
                button.setLayoutParams(lp);
                button.setTextSize(35 - (dimension * 3));
                button.setTextColor(Color.LTGRAY);

                Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ComicRelief.ttf");
                button.setTypeface(font);


                Bitmap square = Bitmap.createBitmap(bitMap, x, y, wButton, hButton);
                Drawable buttonBackground = new BitmapDrawable(getResources(), square).getCurrent();
                button.setBackgroundDrawable(buttonBackground);

                //Set Tag
                //button.setTag(slidingPuzzleGame.getCell(i, j));
				int tagId = counter++;
				button.setTag(tagId);

                mainLayout.addView(button);
                int holeButtonCoords[] = slidingPuzzleGame.getHoleCoords();

                //if (i == holeButtonCoords[0]  && j ==  holeButtonCoords[1] ) {
                //if (slidingPuzzleGame.getCell(i,j) == slidingPuzzleGame.getHoleTag()) {
                if (i == slidingPuzzleGame.getCols() -1 && j == slidingPuzzleGame.getRows()-1) {
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
                int[] btnPosition = slidingPuzzleGame.getCoordsById(tagId);
                if (btnPosition != null) {
                    int iPos = btnPosition[0];
                    int jPos = btnPosition[1];
                    paintViewAt(button, iPos, jPos);
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
        for (int j = 0; j < slidingPuzzleGame.getRows(); j++) {
            for (int i = 0; i < slidingPuzzleGame.getCols(); i++) {

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

        for (int j = 0; j < slidingPuzzleGame.getRows(); j++) {
            for (int i = 0; i < slidingPuzzleGame.getCols(); i++) {

                int idButton = slidingPuzzleGame.getCell(i, j);
                Button button = findButtonByTag(idButton);
                if (button != null){
                    //if (idButton != slidingPuzzleGame.getHoleTag()) {
                        paintViewAt(button, i, j);
                    //}
                }
            }
        }

        if (slidingPuzzleGame.isFinished()) {
            paintLastButton();
        }

        //hideLastButton();
    }

    private void paintViewAt(View view, int i, int j) {
        int baseWidth = view.getWidth();
        int baseHeight = view.getHeight();

        view.setX(baseWidth * i + (PADDING * i));
        view.setY(baseHeight * j + (PADDING * j));
    }

    private int getStickWidth() {
        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int wLayout = mainLayout.getMeasuredWidth(); // - ((cols - 1) * PADDING);

        int wButton = wLayout / slidingPuzzleGame.getCols();

        return wButton;
    }

    private int getStickHeight() {
        AbsoluteLayout mainLayout = (AbsoluteLayout) findViewById(R.id.layoutMain);
        int hLayout = mainLayout.getMeasuredHeight();// - ((rows - 1) * PADDING);
        int hButton = hLayout / slidingPuzzleGame.getRows();

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
        paintViewAt(holeButton,slidingPuzzleGame.getCols() - 1, slidingPuzzleGame.getRows() - 1);
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

