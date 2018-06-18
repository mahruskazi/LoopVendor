package com.team.socero.loopvendor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Mahrus Kazi on 2018-06-10.
 */

public class LoginScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 110) {
            finish();

        }
    }

    public void joinTeam(View view) {
        Intent intent = new Intent(this, OrganizersSignin.class);
        startActivity(intent);

    }

    /**
     * Check if there is an active or soon-to-be-active network connection.
     *
     * @return true if there is no network connection, false otherwise.
     */
    private boolean isOffline() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return !(manager != null
                && manager.getActiveNetworkInfo() != null
                && manager.getActiveNetworkInfo().isConnectedOrConnecting());
    }
}
