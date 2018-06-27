package com.abdul.jigsawpuzzle;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Bitmap> Image;
    Chronometer chronometer;
    long pauseOffSet;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RelativeLayout constraintLayout = findViewById(R.id.layout);
        final ImageView view = findViewById(R.id.imageView);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        chronometer.post(new Runnable() {
            @Override
            public void run() {
                chronometer.start();
                running = true;
            }
        });

        ImageButton play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running){
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffSet);
                    chronometer.start();
                    running = true;
                }
            }
        });

        ImageButton pause = findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running){
                    chronometer.stop();
                    pauseOffSet = SystemClock.elapsedRealtime() - chronometer.getBase();
                    running = false;
                }
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                Image = splitImage(view,2,2);
                for (Bitmap singleImage : Image){
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageBitmap(singleImage);
                    imageView.setOnTouchListener(new TouchFunctionality());
                    constraintLayout.addView(imageView);
                }
            }
        });
    }

    private  ArrayList<Bitmap> splitImage(ImageView imageView, int rows, int columns){
        int pieceHeight;
        int pieceWidth;
        Bitmap bitmap;

        int NoOfPieces = rows*columns;

        ArrayList<Bitmap> bitmapStorageList = new ArrayList<>(NoOfPieces);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        pieceHeight = bitmap.getHeight()/rows;
        pieceWidth = bitmap.getWidth()/columns;

        int yCoordinate =0;

        for (int i=0; i<rows; i++){
            int xCoordinate =0;

            for (int j=0; j<columns; j++){
                bitmapStorageList.add(Bitmap.createBitmap(bitmap, xCoordinate, yCoordinate, pieceWidth, pieceHeight));
                xCoordinate = xCoordinate+pieceWidth;
            }
            yCoordinate = yCoordinate+pieceHeight;
        }

        return bitmapStorageList;
    }
}
