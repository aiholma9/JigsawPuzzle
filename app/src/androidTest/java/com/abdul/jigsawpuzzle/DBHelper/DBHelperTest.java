package com.abdul.jigsawpuzzle.DBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.DIFFICULTY;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.MIN_VAL;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.PIECES;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.PUZZLE_NAME;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.RATIO;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.TABLE_NAME;
import static com.abdul.jigsawpuzzle.DBHelper.DBHelper.TIME_ELASPED;

public class DBHelperTest extends AndroidTestCase {

    private static int mTimeElapsed;
    private static String mDifficulty;
    private static String mPuzzleName;
    private static String mRatio;
    private static String mPiece;
    private static int mMinVal;
    private static long mID;

    public void testDrop(){
        assertTrue(mContext.deleteDatabase(DBHelper.DB_NAME));
    }

    public void testOnCreate(){
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        assertTrue(sqLiteDatabase.isOpen());
        sqLiteDatabase.close();
    }

    public void testAddTime(){
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        mTimeElapsed = 60;
        mDifficulty = "5";
        mPuzzleName = "05";
        mRatio = "Original";
        mPiece = "Square";
        mMinVal = 1;

        String sql = "INSERT INTO "+TABLE_NAME+
                "(" +DBHelper.TIME_ELASPED+ ", " +DIFFICULTY+ ", " +PUZZLE_NAME+ ", " +RATIO+
                ", " +PIECES+ ", " +MIN_VAL+
                ") VALUES (" +mTimeElapsed+ ", '" +mDifficulty+
                "', '" +mPuzzleName+ "', '" +mRatio+ "', '"
                +mPiece+ "', " +mMinVal+ ")";

        sqLiteDatabase.execSQL(sql);
        assertTrue(mID != -1);
    }

    public void testRead() {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        String sql = "SELECT * from "+ TABLE_NAME +" WHERE "+MIN_VAL+ "=" +mMinVal+ " AND " +DIFFICULTY+ " = '"
                +mDifficulty+ "' AND " +PUZZLE_NAME+ " = '" +mPuzzleName+ "' AND "
                +RATIO+ " = '" +mRatio+"' AND " +PIECES+ " = '" +mPiece+ "'";

        sqLiteDatabase.execSQL(sql);
        assertEquals(mMinVal, 1);
    }


    public void testUpdateTime() {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        mMinVal = 0;

        String sql = "UPDATE " +TABLE_NAME+ " SET " +MIN_VAL+ " = " +mMinVal+ " WHERE "+MIN_VAL+ "=" +mMinVal+ " AND " +DIFFICULTY+ " = '"
                +mDifficulty+ "' AND " +PUZZLE_NAME+ " = '" +mPuzzleName+ "' AND "
                +RATIO+ " = '" +mRatio+"' AND " +PIECES+ " = '" +mPiece+ "'";

        sqLiteDatabase.execSQL(sql);
        assertEquals(mMinVal, 0);
        assertEquals(mTimeElapsed, 60);
        assertEquals(mDifficulty,  "5");
        assertEquals(mPuzzleName, "05");
        assertEquals(mRatio, "Original");
        assertEquals(mPiece, "Square");
    }

    public void testReadBestTime() {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null,null,null,null);

        testAddTime();
        assertTrue(cursor.moveToFirst());

        assertEquals(mTimeElapsed, cursor.getInt(1));
        assertEquals(mDifficulty, cursor.getString(2));
        assertEquals(mPuzzleName, cursor.getString(3));
        assertEquals(mRatio, cursor.getString(4));
        assertEquals(mPiece, cursor.getString(5));
        assertEquals(mMinVal, cursor.getInt(6));
    }
}