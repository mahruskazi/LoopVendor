package com.team.socero.loopvendor;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SoceroAPI
{

    // NOTE**** that you’ll make the request with your live or test secret API key, depending on whether you want to get a live or test access token back.
    @POST("token?client_secret=sk_test_eTI4M4VjixNf2VzXD2jXKDmL")
    Call<StripeUser> getStripeUser(@Body RequestBody body);


}
