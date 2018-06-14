package com.team.socero.loopvendor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mahrus Kazi on 2018-06-10.
 */

public class LoginScreen extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        prefs = getSharedPreferences("LoopVendor", Context.MODE_PRIVATE);

    }

    public void stripe_login(View view) {
        Intent s = new Intent(getApplicationContext(), StripeConnectView.class);
        startActivityForResult(s, 105);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 105){
            Log.d("Stripe", "Stripe user token: " + data.getStringExtra("token"));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", data.getStringExtra("token"));
                jsonObject.put("grant_type", "authorization_code");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://connect.stripe.com/oauth/")
                    .addConverterFactory(GsonConverterFactory.create())
                    //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            //APIModule.changeApiBaseUrl("https://www.googleapis.com/urlshortener/v1/url/");
            SoceroAPI stripeUser = retrofit.create(SoceroAPI.class);//APIModule.createService(SoceroAPI.class, baroServiceProvider.getAuthToken().getToken());

            stripeUser.getStripeUser(body).enqueue(new Callback<StripeUser>()
            {
                @Override
                public void onResponse(Call<StripeUser> call, Response<StripeUser> response)
                {
                    if (response.body() != null)
                    {
                        Log.d("Stripe", "Stripe User: " + response.body().getStripeUserId());
                        prefs.edit().putString("stripe_account", response.body().getStripeUserId()).apply();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Log.d("Stripe", "Nothing");
                    }
                }

                @Override
                public void onFailure(Call<StripeUser> call, Throwable throwable)
                {

                }
            });

        }else if( resultCode == RESULT_CANCELED && requestCode == 105){
            if(data != null)
                Log.d("Stripe", "Stripe error: " + data.getStringExtra("token"));
            else
                Log.d("Stripe", "Stripe Error: CANCELED" );
        }
    }
}
