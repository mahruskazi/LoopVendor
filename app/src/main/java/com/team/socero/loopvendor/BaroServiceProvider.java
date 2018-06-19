package com.team.socero.loopvendor;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class BaroServiceProvider {
    SoceroAPI soceroAPIService;
    TokenProvider tokenProvider;
    GoogleSignInAccount account;

    @Inject public BaroServiceProvider(SoceroAPI soceroAPIService, TokenProvider tokenProvider) {
        this.soceroAPIService = soceroAPIService;
        this.tokenProvider = tokenProvider;
    }

    public Observable<SoceroAPI> availableService() {
        return Observable.just(soceroAPIService);
    }



    public void setAuthToken(String token) {
        tokenProvider.setToken(token);
    }

    public TokenProvider getAuthToken() { return tokenProvider;}

    public void setGoogleAccount(GoogleSignInAccount account) { this.account = account; }

    public GoogleSignInAccount getGoogleAccount() { return account;}
}
