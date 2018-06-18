package com.team.socero.loopvendor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
 * Created by Mahrus Kazi on 2018-06-15.
 */

public class OrganizersSignin extends AppCompatActivity {

    private static final String TAG = "OrganizersSignin";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /*if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }*/

        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        // [END initialize_fblogin]

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //TODO add a check to see if user is already signed in //updateUI(currentUser);
    }

    public void emailLogin(View view) {
        Log.d(TAG, "emailLogin: Clicked");
        Intent intent = new Intent(this, EmailSigninActivity.class);
        startActivityForResult(intent, 110);
    }

    public void googleLogin(View view) {
        Log.d(TAG, "googleLogin: Clicked");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void facebookLogin(View view) {
        LoginButton btn = (LoginButton) view.findViewById(R.id.facebook_login_button);
        btn.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 110) {
            finish();

        }else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        } else if (resultCode == RESULT_OK && requestCode == 105) {
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

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            validateUser(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(OrganizersSignin.this, "Authentication failure", Toast.LENGTH_LONG).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
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
                                Toast.makeText(OrganizersSignin.this, "Password/Email is incorrect", Toast.LENGTH_LONG).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(TAG, "onComplete: Password is wrong");
                                Toast.makeText(OrganizersSignin.this, "Password/Email is incorrect", Toast.LENGTH_LONG).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        } else {
                            Log.d(TAG, "signinUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            validateUser(user);
                        }
                    }
                });
    }

    private void validateUser(FirebaseUser user) {
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
