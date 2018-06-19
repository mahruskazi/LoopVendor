package com.team.socero.loopvendor;

/**
 * Created by Albin on 2017-05-28.
 */

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenProvider
{

    public String token;

    @Inject public TokenProvider() {
        token = "";
    }

    public void setToken(String token) {

        this.token = token;

    }

    public String getToken() {

        return this.token;

    }

}