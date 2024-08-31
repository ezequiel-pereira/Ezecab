package com.ezedev.ezecab.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IDirectionsAPI {
    @GET
    Call<String> getDirections(@Url String url);
}
