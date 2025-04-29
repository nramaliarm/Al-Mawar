package com.example.e_almawar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Header;

public interface ImgurService {
    @POST("3/image")
    Call<ImgurResponse> uploadImage(
            @Header("Authorization") String clientId,
            @Part MultipartBody.Part image
    );
}

