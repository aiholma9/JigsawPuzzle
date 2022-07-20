package com.abdul.jigsawpuzzle.Common;

import com.abdul.jigsawpuzzle.Interface.IComputerVision;
import com.abdul.jigsawpuzzle.Model.CategoryItem;
import com.abdul.jigsawpuzzle.Model.PuzzleItem;
import com.abdul.jigsawpuzzle.Utils.RetrofitClient;

public class Common {
    public static final String CATEGORY = "Categories";
    public static final String PUZZLES = "Puzzles";
    public static final String CHANNEL_ID = "Notify";
    public static final String PUSH = "pushNotification";
    public static final int PICK_IMAGE_REQUEST = 1002;
    public static String CATEGORY_SELECTED;
    public static String CATEGORY_ID_SELECTED;

    public static PuzzleItem puzzleItem = new PuzzleItem();

    public static String SELECT_KEY;

    public static final int SIGN_IN_REQUEST_CODE = 1000;

    //API
    public static String BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/";
    public static IComputerVision getComputerVisionAPI(){
        return RetrofitClient.getRetrofit(BASE_URL).create(IComputerVision.class);
    }

    public static String getAPIAdultEndPoint(){
        return new StringBuilder(BASE_URL+"analyze?visualFeatures=Adult&language=en").toString();
    }
}
