package com.team.socero.loopvendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

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
 * Created by Mahrus Kazi on 2018-06-17.
 */

public class EmailSignupActivity extends AppCompatActivity {

    private static final String TAG = "EmailSignupActivity";
    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signup);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_edit);
        password = findViewById(R.id.password_edit);
        confirmPassword = findViewById(R.id.confirm_password_edit);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(TAG, "onComplete: weak_password");
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: malformed_email");
                                Toast.makeText(EmailSignupActivity.this, "Email is not valid", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(TAG, "onComplete: exist_email");
                                Toast.makeText(EmailSignupActivity.this, "Email already exists", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        } else {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            FireBaseDataBase.initialize(user.getUid());
                            FireBaseDataBase dataBase = new FireBaseDataBase();

                            Log.d(TAG, "onDataChange: Stripe already Connected");
                            Profile newProfile = new Profile();
                            newProfile.setUserID(user.getUid());
                            newProfile.setDisplayName(user.getDisplayName());
                            newProfile.setEmail(user.getEmail());
                            newProfile.setPhoneNumber(user.getPhoneNumber());
                            newProfile.setAccessLevel(0);

                            dataBase.addProfile(newProfile);
                            Log.d(TAG, "onComplete: Not connected");

                            Intent s = new Intent(getApplicationContext(), StripeConnectView.class);
                            startActivityForResult(s, 105);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 105) {
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

            stripeUser.getStripeUser(body).enqueue(new Callback<StripeUser>() {
                @Override
                public void onResponse(Call<StripeUser> call, Response<StripeUser> response) {
                    if (response.body() != null) {
                        Log.d("Stripe", "Stripe User: " + response.body().getStripeUserId());
                        FireBaseDataBase dataBase = new FireBaseDataBase();
                        dataBase.addStripeID(response.body().getStripeUserId());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("Stripe", "Nothing");
                    }
                }

                @Override
                public void onFailure(Call<StripeUser> call, Throwable throwable) {

                }
            });

        } else if (resultCode == RESULT_CANCELED && requestCode == 105) {
            if (data != null)
                Log.d("Stripe", "Stripe error: " + data.getStringExtra("token"));
            else
                Log.d("Stripe", "Stripe Error: CANCELED");
        }
    }

    private boolean validateForm() {

        if(email.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input your email", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input a password", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(confirmPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(!password.getText().toString().contentEquals(confirmPassword.getText().toString())){
            Toast.makeText(this, "Please passwords dont match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void register(View view) {
        if(validateForm()){
            createAccount(email.getText().toString(), password.getText().toString());
        }
    }
}
