package com.abdul.jigsawpuzzle.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.DBHelper.DBHelper;
import com.abdul.jigsawpuzzle.Model.BestTime;
import com.abdul.jigsawpuzzle.Model.PuzzleItem;
import com.abdul.jigsawpuzzle.Model.PuzzlePiece;
import com.abdul.jigsawpuzzle.R;
import com.abdul.jigsawpuzzle.Touch.TouchFunctionality;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    Dialog dialog, endDialog;

    ImageView imageView, image_preview, image_ghost;
    Chronometer chronometer;
    TextView textView, textViewTime, timeLabel;

    ArrayList<PuzzlePiece> pieces;
    private boolean running;
    long pauseOffset;
    String tweet;

    FloatingActionButton btn_preview, btn_ghost, btn_restart;
    FloatingActionButton btn_white, btn_black, btn_magenta, btn_blue;

    FloatingActionButton btn_fbShare, btn_tweet, btn_fbShareEnd, btn_tweetEnd;

    Button btn_pause, btn_closeEnd;

    RelativeLayout layout;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        dbHelper = new DBHelper(this);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pause_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        endDialog = new Dialog(this);
        endDialog.setContentView(R.layout.end_game);
        endDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        layout = (RelativeLayout) findViewById(R.id.layout);
        final RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout);

        imageView = findViewById(R.id.imageView);
        image_preview = (ImageView)findViewById(R.id.imageViewPreview);
        image_ghost = (ImageView)findViewById(R.id.imageView_ghost);

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        textView = (TextView)findViewById(R.id.playPause);
        textViewTime = (TextView)endDialog.findViewById(R.id.time);
        timeLabel = (TextView)endDialog.findViewById(R.id.timeLabel);

        btn_preview = (FloatingActionButton) findViewById(R.id.btn_preview);
        btn_ghost = (FloatingActionButton) findViewById(R.id.btn_ghost);
        btn_restart = (FloatingActionButton)findViewById(R.id.btn_restart);

        btn_white = (FloatingActionButton)findViewById(R.id.btn_white);
        btn_black = (FloatingActionButton)findViewById(R.id.btn_black);
        btn_magenta = (FloatingActionButton)findViewById(R.id.btn_magenta);
        btn_blue = (FloatingActionButton)findViewById(R.id.btn_blue);

        btn_fbShare = (FloatingActionButton)findViewById(R.id.btn_fbShare);
        btn_tweet = (FloatingActionButton) findViewById(R.id.btn_tweet);
        btn_fbShareEnd = (FloatingActionButton)endDialog.findViewById(R.id.btn_fbShareEnd);
        btn_tweetEnd = (FloatingActionButton) endDialog.findViewById(R.id.btn_tweetEnd);

        btn_pause = (Button)dialog.findViewById(R.id.close);

        btn_closeEnd = (Button)endDialog.findViewById(R.id.btn_closeEnd);

        loadImage(Common.puzzleItem.getimageUrl());

        imageView.setVisibility(View.INVISIBLE);
        image_preview.setVisibility(View.INVISIBLE);
        image_ghost.setVisibility(View.INVISIBLE);

        chronometer.setFormat("%s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        btn_pause.setOnClickListener(this);
        textView.setOnClickListener(this);

        btn_preview.setOnClickListener(this);
        btn_ghost.setOnClickListener(this);
        btn_restart.setOnClickListener(this);

        btn_fbShare.setOnClickListener(this);
        btn_tweet.setOnClickListener(this);
        btn_fbShareEnd.setOnClickListener(this);
        btn_tweetEnd.setOnClickListener(this);

        btn_closeEnd.setOnClickListener(this);

        btn_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        btn_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setBackgroundColor(Color.parseColor("#000000"));
            }
        });

        btn_magenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setBackgroundColor(Color.parseColor("#31276f"));
            }
        });

        btn_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setBackgroundColor(Color.parseColor("#0080b3"));
            }
        });


        chronometer.post(new Runnable() {
            @Override
            public void run() {
                chronometer.start();
                running = true;
            }
        });

        //Delays displaying of puzzles
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pieces = splitImage();
                Collections.shuffle(pieces);
                TouchFunctionality touchFunctionality = new TouchFunctionality(GameActivity.this);
                for (PuzzlePiece piece : pieces) {
                    piece.setOnTouchListener(touchFunctionality);
                    relativeLayout.addView(piece);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)piece.getLayoutParams();
                    layoutParams.leftMargin = new Random().nextInt(layout.getWidth() - piece.pieceWidth); // at the top center of the screen
                    piece.setLayoutParams(layoutParams);
                }
            }
        }, 2000);
    }

    //Method to count played Puzzles
    private void played() {
        FirebaseDatabase.getInstance().getReference(Common.PUZZLES).child(Common.SELECT_KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("played")){//if played is already in the database.
                            PuzzleItem puzzleItem = dataSnapshot.getValue(PuzzleItem.class);
                            long played = Long.valueOf(puzzleItem.getPlayed()) + 1;

                            Map<String, Object> updatePlayed = new HashMap<>();
                            updatePlayed.put("played", played);

                            FirebaseDatabase.getInstance().getReference(Common.PUZZLES).child(Common.SELECT_KEY)
                                    .updateChildren(updatePlayed).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                        else {//played not in the database especially for
                            Map<String, Object> updatePlayed = new HashMap<>();
                            updatePlayed.put("played", Long.valueOf(1));

                            FirebaseDatabase.getInstance().getReference(Common.PUZZLES).child(Common.SELECT_KEY)
                                    .updateChildren(updatePlayed).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //Loading images from the Database into imageViews
    private void loadImage(String s) {
        //selected stretch from the database
        if (("Stretch").equals(getIntent().getStringExtra("Ratio"))){
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .resize(1300, 2000).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .resize(1300, 2000).into(image_preview, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .resize(1300, 2000).into(image_ghost, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }

        //selected original from the database
        else if (("Original").equals(getIntent().getStringExtra("Ratio"))){
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .into(image_preview, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
            Picasso.with(this).load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                    .into(image_ghost, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });
        }
    }

    //End game
    public void checkGame(){
        if (isGameOver()){
            played();
            textViewTime.setText(chronometer.getText());
            chronometer.stop();
            endDialog.show();
        }
    }

    private boolean isGameOver() {
        for (PuzzlePiece puzzlePiece : pieces){
            if (puzzlePiece.canMove)
                return false;
        }
        return true;
    }

    //Split images
    private ArrayList<PuzzlePiece> splitImage() {

        int rows = Integer.parseInt(getIntent().getStringExtra("Difficulty"));
        int columns = Integer.parseInt(getIntent().getStringExtra("Difficulty"));

        //calculated number of pieces
        int NoOfPieces = rows * columns;

        imageView = findViewById(R.id.imageView);
        ArrayList<PuzzlePiece> pieces = new ArrayList<>(NoOfPieces);

        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        int pieceWidth = bitmap.getWidth()/columns;
        int pieceHeight = bitmap.getHeight()/rows;


            int yCoordinate = 0;
            for (int i = 0; i < rows; i++ ) { //puzzle pieces into rows
                int xCoordinate = 0;
                for (int j = 0; j < columns; j++){ //puzzle pieces into columns
                    int offSetX = 0;
                    int offSetY = 0;

                    if (j > 0 )
                        offSetX = pieceWidth/3;

                    if (i > 0)
                        offSetY = pieceHeight/3;

                    Bitmap pieceBitmap = Bitmap.createBitmap(bitmap, xCoordinate - offSetX, yCoordinate - offSetY, pieceWidth + offSetX, pieceHeight + offSetY);
                    PuzzlePiece puzzlePiece = new PuzzlePiece(getApplicationContext());
                    puzzlePiece.setImageBitmap(pieceBitmap);
                    puzzlePiece.xCoordinate = xCoordinate - offSetX + imageView.getLeft();
                    puzzlePiece.yCoordinate = yCoordinate - offSetY + imageView.getTop();
                    puzzlePiece.pieceWidth = pieceWidth + offSetX;
                    puzzlePiece.pieceHeight = pieceHeight + offSetY;

                    Bitmap jigsaw = Bitmap.createBitmap(pieceWidth + offSetX, pieceHeight + offSetY, Bitmap.Config.ARGB_8888);

                    int bumpSize = pieceHeight/4;
                    Canvas canvas = new Canvas(jigsaw);
                    Path path = new Path();
                    path.moveTo(offSetX, offSetY);

                    if (("Square").equals(getIntent().getStringExtra("Piece"))) {
                        if (i == 0) {
                            path.lineTo(pieceBitmap.getWidth(), offSetY);
                        } else {
                            path.lineTo(offSetX + (pieceBitmap.getWidth() - offSetX), offSetY);
                            path.cubicTo(offSetX + (pieceBitmap.getWidth() - offSetX), offSetY - bumpSize,
                                    offSetX + (pieceBitmap.getWidth() - offSetX), offSetY - bumpSize,
                                    offSetX + (pieceBitmap.getWidth() - offSetX), offSetY);
                            path.lineTo(pieceBitmap.getWidth(), offSetY);
                        }

                        if (j == columns - 1) {
                            path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                        } else {
                            path.lineTo(pieceBitmap.getWidth(), offSetY + (pieceBitmap.getHeight() - offSetY));
                            path.cubicTo(pieceBitmap.getWidth() - bumpSize,offSetY + (pieceBitmap.getHeight() - offSetY),
                                    pieceBitmap.getWidth() - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY), pieceBitmap.getWidth(),
                                    offSetY + (pieceBitmap.getHeight() - offSetY));
                            path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                        }

                        if (i == rows - 1) {
                            path.lineTo(offSetX, pieceBitmap.getHeight());
                        } else {
                            path.lineTo(offSetX + (pieceBitmap.getWidth() - offSetX), pieceBitmap.getHeight());
                            path.cubicTo(offSetX + (pieceBitmap.getWidth() - offSetX),
                                    pieceBitmap.getHeight() - bumpSize, offSetX + (pieceBitmap.getWidth() - offSetX),
                                    pieceBitmap.getHeight() - bumpSize, offSetX + (pieceBitmap.getWidth() - offSetX), pieceBitmap.getHeight());
                            path.lineTo(offSetX, pieceBitmap.getHeight());
                        }

                        if (j == 0) {
                            path.close();
                        } else {
                            path.lineTo(offSetX, offSetY + (pieceBitmap.getHeight() - offSetY));
                            path.cubicTo(offSetX - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY),
                                    offSetX - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY), offSetX,
                                    offSetY + (pieceBitmap.getHeight() - offSetY));
                            path.close();
                        }
                    }

                    else if (("Jigsaw").equals(getIntent().getStringExtra("Piece"))) {
                        /*
                        Sourced from https://dragosholban.com/2018/03/09/how-to-build-a-jigsaw-puzzle-android-game/
                        15/08/19
                        Start
                        The code below responsible for cutting images into jigsaw shapes
                         */
                        if (i == 0) {
                            // top side piece
                            path.lineTo(pieceBitmap.getWidth(), offSetY);
                        } else {
                            // top bump
                            path.lineTo(offSetX + (pieceBitmap.getWidth() - offSetX) / 3, offSetY);
                            path.cubicTo(offSetX + (pieceBitmap.getWidth() - offSetX) / 6, offSetY - bumpSize,
                                    offSetX + (pieceBitmap.getWidth() - offSetX) / 6 * 5, offSetY - bumpSize,
                                    offSetX + (pieceBitmap.getWidth() - offSetX) / 3 * 2, offSetY);
                            path.lineTo(pieceBitmap.getWidth(), offSetY);
                        }

                        if (j == columns - 1) {
                            // right side piece
                            path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                        } else {
                            // right bump
                            path.lineTo(pieceBitmap.getWidth(), offSetY + (pieceBitmap.getHeight() - offSetY) / 3);
                            path.cubicTo(pieceBitmap.getWidth() - bumpSize,offSetY + (pieceBitmap.getHeight() - offSetY) / 6,
                                    pieceBitmap.getWidth() - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY) / 6 * 5, pieceBitmap.getWidth(),
                                    offSetY + (pieceBitmap.getHeight() - offSetY) / 3 * 2);
                            path.lineTo(pieceBitmap.getWidth(), pieceBitmap.getHeight());
                        }

                        if (i == rows - 1) {
                            // bottom side piece
                            path.lineTo(offSetX, pieceBitmap.getHeight());
                        } else {
                            // bottom bump
                            path.lineTo(offSetX + (pieceBitmap.getWidth() - offSetX) / 3*2, pieceBitmap.getHeight());
                            path.cubicTo(offSetX + (pieceBitmap.getWidth() - offSetX) / 6*5,
                                    pieceBitmap.getHeight() - bumpSize, offSetX + (pieceBitmap.getWidth() - offSetX) / 6,
                                    pieceBitmap.getHeight() - bumpSize, offSetX + (pieceBitmap.getWidth() - offSetX) / 3, pieceBitmap.getHeight());
                            path.lineTo(offSetX, pieceBitmap.getHeight());
                        }

                        if (j == 0) {
                            // left side piece
                            path.close();
                        } else {
                            // left bump
                            path.lineTo(offSetX, offSetY + (pieceBitmap.getHeight() - offSetY) / 3 * 2);
                            path.cubicTo(offSetX - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY) / 6 * 5,
                                    offSetX - bumpSize, offSetY + (pieceBitmap.getHeight() - offSetY) / 6, offSetX,
                                    offSetY + (pieceBitmap.getHeight() - offSetY) / 3);
                            path.close();
                        }
                        /*
                        https://dragosholban.com/2018/03/09/how-to-build-a-jigsaw-puzzle-android-game/
                        End
                         */
                    }

                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.FILL);

                    canvas.drawPath(path, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(pieceBitmap, 0,0, paint);

                    Paint border = new Paint();
                    border.setColor(0X80FFFFFF);
                    border.setStyle(Paint.Style.STROKE);
                    border.setStrokeWidth(3.0f);
                    canvas.drawPath(path, border);

                    puzzlePiece.setImageBitmap(jigsaw);

                    pieces.add(puzzlePiece);
                    xCoordinate += pieceWidth;
                }
                yCoordinate += pieceHeight;

            }
        return pieces;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.close:
                if (!running) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    textView.setText("||pause||");
                    btn_preview.setClickable(true);
                    btn_ghost.setClickable(true);
                    running = true;
                    dialog.dismiss();
                }
                break;

            case R.id.playPause:
                if (running) {
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    textView.setText("|| play ||");
                    btn_preview.setClickable(false);
                    btn_ghost.setClickable(false);
                    dialog.show();
                    running = false;
                }
                break;

            case R.id.btn_preview:
                if (image_preview.getVisibility() == View.INVISIBLE && image_ghost.getVisibility() == View.INVISIBLE)
                    image_preview.setVisibility(View.VISIBLE);
                else if (image_preview.getVisibility() == View.VISIBLE)
                    image_preview.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_ghost:
                if (image_ghost.getVisibility() == View.INVISIBLE && image_preview.getVisibility() == View.INVISIBLE)
                    image_ghost.setVisibility(View.VISIBLE);
                else if (image_ghost.getVisibility() == View.VISIBLE)
                    image_ghost.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_restart:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;

            case R.id.btn_fbShare:
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote("This game is really good").setContentUrl(Uri.parse(Common.puzzleItem.imageUrl)).build();

                if (ShareDialog.canShow(ShareLinkContent.class))
                    shareDialog.show(shareLinkContent );
                break;

            case R.id.btn_tweet:
                tweet = "https://twitter.com/intent/tweet?text= &url= This game is really good "+Common.puzzleItem.imageUrl;
                Uri uri = Uri.parse(tweet);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;

            case R.id.btn_fbShareEnd:
                ShareLinkContent shareLinkContentTime = new ShareLinkContent.Builder()
                        .setQuote("This is my time: " +chronometer.getText()).setContentUrl(Uri.parse(Common.puzzleItem.imageUrl)).build();

                if (ShareDialog.canShow(ShareLinkContent.class))
                    shareDialog.show(shareLinkContentTime);
                break;

            case R.id.btn_tweetEnd:
                tweet = "https://twitter.com/intent/tweet?text= &url= This is my time: "+chronometer.getText()+ " "+Common.puzzleItem.imageUrl;
                Uri uriTime = Uri.parse(tweet);
                startActivity(new Intent(Intent.ACTION_VIEW, uriTime));
                break;

            case R.id.btn_closeEnd:
                String difficulty = getIntent().getStringExtra("Difficulty");
                String ratio = getIntent().getStringExtra("Ratio");
                String pieces = getIntent().getStringExtra("Piece");

                String timeElasped = chronometer.getText().toString();

                int quoteInd = timeElasped.indexOf(":");

                int min = Integer.valueOf(timeElasped.substring(0, quoteInd));
                int sec = Integer.valueOf(timeElasped.substring(++quoteInd, timeElasped.length()));

                int time = (((min*60) + sec));

               BestTime bestTime = new BestTime(time, difficulty, Common.SELECT_KEY, ratio, pieces);

                int dbTime = dbHelper.read(bestTime);
                if (dbTime == -1){
                    dbHelper.addTime(bestTime, 1);
                }

                else {
                    if (time <= dbTime){
                        dbHelper.updateTime(bestTime);
                        dbHelper.addTime(bestTime, 1);
                    }

                    else {
                        dbHelper.addTime(bestTime, 0);
                    }
                }
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
