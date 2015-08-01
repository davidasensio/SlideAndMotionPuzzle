package com.handysparksoft.slideandmotionpuzzle;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


public class ShowCurrentImage extends ActionBarActivity {
    private Uri imageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_current_image);

        imageUri = (Uri) getIntent().getParcelableExtra("current_image_uri");
        if (imageUri != null) {
            try {
                ImageView imageViewCurrentImage = (ImageView) findViewById(R.id.imageView2);
                Bitmap myImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageViewCurrentImage.setImageBitmap(myImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Button btnBack = (Button) findViewById(R.id.btnBackShowCurrentImage);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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

        return super.onOptionsItemSelected(item);
    }
}
