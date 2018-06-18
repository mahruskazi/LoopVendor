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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

public class EmailSigninActivity extends AppCompatActivity {
    private static final String TAG = "EmailSigninActivity";

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_signin);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_signin_edit);
        password = findViewById(R.id.password_signin_edit);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void signup(View view) {
        Intent intent = new Intent(this, EmailSignupActivity.class);
        startActivity(intent);

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidUserException invalidUser)
                            {
                                Log.d(TAG, "onComplete: Email does not exist");
                                Toast.makeText(EmailSigninActivity.this, "Password/Email is incorrect", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: Password is wrong");
                                Toast.makeText(EmailSigninActivity.this, "Password/Email is incorrect", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        } else {
                            Log.d(TAG, "signinUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "onComplete: " + user.getUid());
                            FireBaseDataBase.initialize(user.getUid());
                            FireBaseDataBase dataBase = new FireBaseDataBase();

                            dataBase.profileReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                    Profile profile = dataSnapshot.getValue(Profile.class);
                                    Log.d(TAG, "onDataChange: Here");

                                    if(profile == null || profile.getStripeID() == null) {
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
                                    else{
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        setResult(RESULT_OK);
                                        startActivityForResult(intent, 110);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
        // [END sign_in_with_email]
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
                        setResult(RESULT_OK);
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

        return true;
    }

    public void register(View view) {
        if(validateForm()){
            signIn(email.getText().toString(), password.getText().toString());
        }
    }
}
