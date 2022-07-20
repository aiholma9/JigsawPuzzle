package com.abdul.jigsawpuzzle.Activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.DBHelper.DBHelper;
import com.abdul.jigsawpuzzle.R;
import com.abdul.jigsawpuzzle.Utils.DatabaseUtils;
import com.squareup.picasso.Picasso;


public class CreateActivity extends AppCompatActivity {

    RadioGroup radioGroup, radioRatio;
    RadioButton radioPiece, Ratio;
    Button btnCreate, btnCancel;
    TextView txtEasy, txtHard, txtDifficult, bestTime;
    SeekBar seekBar;

    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView imageView;
    CoordinatorLayout rootLayout;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootlayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        imageView = (ImageView)findViewById(R.id.imageThumb);
        Picasso.with(this)
                .load(Common.puzzleItem.getimageUrl()).error(R.drawable.ic_terrain_black_24dp)
                .into(imageView);

        radioGroup = (RadioGroup)findViewById(R.id.pieces);
        radioRatio = (RadioGroup)findViewById(R.id.ratio);


        btnCreate = (Button)findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        txtHard = (TextView)findViewById(R.id.txtHard);
        txtEasy = (TextView)findViewById(R.id.txtEasy);
        txtDifficult = (TextView)findViewById(R.id.txtLevel);

        bestTime = (TextView)findViewById(R.id.bestTime);
        timeDefault();

        dbHelper = new DBHelper(this);

        bestTime.setText(stringTime());

        seekBar = (SeekBar)findViewById(R.id.seekerbar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int selected = radioRatio.getCheckedRadioButtonId();

                radioPiece = (RadioButton)findViewById(selectedId);
                Ratio = (RadioButton)findViewById(selected);

                Intent intent = new Intent(CreateActivity.this, GameActivity.class);
                intent.putExtra("Piece", radioPiece.getText());
                intent.putExtra("Ratio", Ratio.getText());
                intent.putExtra("Difficulty", txtDifficult.getText().toString());
                timeDefault();
                startActivity(intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              seekBar.setProgress(2);
              txtDifficult.setText(String.valueOf(seekBar.getProgress()));
              radioGroup.check(R.id.radioSquare);
              radioRatio.check(R.id.radioFit);
              timeDefault();
              bestTime.setText(stringTime());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                timeElapsed();
                bestTime.setText(stringTime());
            }
        });

        radioRatio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                timeElapsed();
                bestTime.setText(stringTime());
            }
        });

        txtEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() - 1);
                txtDifficult.setText(String.valueOf(seekBar.getProgress()));

                timeElapsed();
                bestTime.setText(stringTime());
            }
        });

        txtHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress()+1);
                txtDifficult.setText(String.valueOf(seekBar.getProgress()));

                timeElapsed();
                bestTime.setText(stringTime());
            }
        });


    }


    //Changes time gotten from database in seconds into minutes and seconds
    public String stringTime(){
        int time = dbHelper.readBestTime();
         return String.format("%02d:%02d",(Integer) time/60, (Integer) time%60);
    }

    //Update shared preferences
    public void timeElapsed(){
        int selectedId = radioGroup.getCheckedRadioButtonId();
        int selected = radioRatio.getCheckedRadioButtonId();

        radioPiece = (RadioButton)findViewById(selectedId);
        Ratio = (RadioButton)findViewById(selected);

        DatabaseUtils.putStringInPreferences(CreateActivity.this, radioPiece.getText().toString(), "Piece", "MyPref");
        DatabaseUtils.putStringInPreferences(CreateActivity.this, Ratio.getText().toString(), "Ratio", "MyPref");
        DatabaseUtils.putStringInPreferences(CreateActivity.this, txtDifficult.getText().toString(), "Difficulty", "MyPref");
    }

    //Reset shared preferences
    public void timeDefault(){
        DatabaseUtils.putStringInPreferences(CreateActivity.this, "Square", "Piece", "MyPref");
        DatabaseUtils.putStringInPreferences(CreateActivity.this, "Original", "Ratio", "MyPref");
        DatabaseUtils.putStringInPreferences(CreateActivity.this, "2", "Difficulty", "MyPref");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
