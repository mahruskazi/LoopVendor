package com.team.socero.loopvendor;

/**
 * Created by Mahrus Kazi on 2018-05-24.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeUser {

    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("livemode")
    @Expose
    private Boolean livemode;
    @SerializedName("stripe_user_id")
    @Expose
    private String stripeUserId;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public String getStripeUserId() {
        return stripeUserId;
    }

    public void setStripeUserId(String stripeUserId) {
        this.stripeUserId = stripeUserId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
