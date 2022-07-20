package com.abdul.jigsawpuzzle.Interface;

import com.abdul.jigsawpuzzle.Model.ComputerVision;
import com.abdul.jigsawpuzzle.Model.URLUpload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

//
public interface  IComputerVision {
    @Headers({
            "Content-Type:application/json",
            "Ocp-Apim-Subscription-Key:8ac457c710224c229b4bbfa3f083d449"
    })
    @POST
    Call<ComputerVision> analyzeImage(@Url String apiEndpoint, @Body URLUpload urlUpload);
}
