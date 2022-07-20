package com.abdul.jigsawpuzzle.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.Model.BestTime;
import com.abdul.jigsawpuzzle.Utils.DatabaseUtils;


public class DBHelper extends SQLiteOpenHelper{

    public static final  String DB_NAME = "PuzzleTime.db";
    private static final  int DB_VERSION = 3;

    public static final  String TABLE_NAME = "PuzzleTime";

    public static final  String ID = "Id";
    public static final String TIME_ELASPED = "TimeElasped";
    public static final  String DIFFICULTY= "Difficulty";
    public static final  String PUZZLE_NAME = "PuzzleName";
    public static final  String RATIO = "Ratio";
    public static final  String PIECES = "Pieces";
    public static final  String MIN_VAL = "isMin";

    public Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

    }

    //Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                "(" +
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TIME_ELASPED+ " INTEGER, " +
                DIFFICULTY+ " TEXT," +
                PUZZLE_NAME+ " TEXT," +
                RATIO+ " TEXT," +
                PIECES+ " TEXT," +
                MIN_VAL+ " INTEGER" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    //Insert time
    public void addTime(BestTime bestTime, int value){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String sql = "INSERT INTO "+TABLE_NAME+
                        "(" +TIME_ELASPED+ ", " +DIFFICULTY+ ", " +PUZZLE_NAME+ ", " +RATIO+
                        ", " +PIECES+ ", " +MIN_VAL+
                        ") VALUES (" +bestTime.getTimeElapsed()+ ", '" +bestTime.getDifficulty()+
                        "', '" +bestTime.getPuzzleName()+ "', '" +bestTime.getRatio()+ "', '"
                        +bestTime.getPieces()+ "', " +value+ ")";

        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
    }

    //Select time
    public int read(BestTime bestTime){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int min = 1;
        int returnVal = -1;
        String sql = "SELECT * from "+ TABLE_NAME +" WHERE "+MIN_VAL+ "=" +min+ " AND " +DIFFICULTY+ " = '"
                +bestTime.getDifficulty()+ "' AND " +PUZZLE_NAME+ " = '" +bestTime.getPuzzleName()+ "' AND "
                +RATIO+ " = '" +bestTime.getRatio()+"' AND " +PIECES+ " = '" +bestTime.getPieces()+ "'";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        if (cursor.moveToFirst()){
            do {
                // Passing values
                returnVal  = cursor.getInt(1);
                // Read next value
            } while(cursor.moveToNext());
        }
        return returnVal;
    }

    //Update time
    public void updateTime(BestTime bestTime){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int min = 0;
        String sql = "UPDATE " +TABLE_NAME+ " SET " +MIN_VAL+ " = " +min+ " WHERE "+MIN_VAL+ "=" +min+ " AND " +DIFFICULTY+ " = '"
                +bestTime.getDifficulty()+ "' AND " +PUZZLE_NAME+ " = '" +bestTime.getPuzzleName()+ "' AND "
                +RATIO+ " = '" +bestTime.getRatio()+"' AND " +PIECES+ " = '" +bestTime.getPieces()+ "'";
        sqLiteDatabase.execSQL(sql);
    }

    //Select best time
    public int readBestTime(){
        int timeElapsed = 0;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int min = 1;

        String piece = DatabaseUtils.getStringFromPreferences(context, null, "Piece", "MyPref");
        String ratio = DatabaseUtils.getStringFromPreferences(context, null, "Ratio", "MyPref");
        String difficulty = DatabaseUtils.getStringFromPreferences(context, null, "Difficulty", "MyPref");

        String sql = "SELECT "+TIME_ELASPED+ " from " +TABLE_NAME+
                " WHERE " +MIN_VAL+ " = " +min+ " AND " +PUZZLE_NAME+ " = '" + Common.SELECT_KEY+ "' AND "
                +DIFFICULTY+ " = '" +difficulty+ "' AND "
                +RATIO+ " = '" +ratio+"' AND " +PIECES+ " = '" +piece+ "'";

        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                timeElapsed  = cursor.getInt(0);
            } while(cursor.moveToNext());
        }
        return timeElapsed;
    }


}
