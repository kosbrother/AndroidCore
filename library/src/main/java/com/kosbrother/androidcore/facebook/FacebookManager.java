package com.kosbrother.androidcore.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

public class FacebookManager {

    public static final String TAG = "FacebookManager";

    private static FacebookManager instance;
    private FacebookListener listener;

    private FacebookManager() {
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                if (oldAccessToken == null) {
                    handleLoginResult(newAccessToken);
                } else if (newAccessToken == null) {
                    listener.onFbLogout();
                }
            }
        };
    }

    public static FacebookManager getInstance() {
        if (instance == null) {
            instance = new FacebookManager();
        }
        return instance;
    }

    public void setListener(FacebookListener listener) {
        this.listener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackManager.Factory.create().onActivityResult(requestCode, resultCode, data);
    }

    private void handleLoginResult(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        handleFbRequestResult(object);
                    }
                });

        request.setParameters(getRequestParameters());
        request.executeAsync();
    }

    private Bundle getRequestParameters() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender");
        return parameters;
    }

    private void handleFbRequestResult(JSONObject object) {
        listener.onFbRequestCompleted(object);
    }

    public interface FacebookListener {
        void onFbRequestCompleted(JSONObject object);

        void onFbLogout();
    }
}
