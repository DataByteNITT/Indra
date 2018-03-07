package com.databyte.indra.remote;

import com.databyte.indra.SpeechQueryCreator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("/api")
    Call<SpeechQueryCreator>  savePost(@Query("text") String speech);
}
