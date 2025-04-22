package com.plugins.forgerockauth;

import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.SSOToken;
import org.forgerock.android.auth.exception.AuthenticationException;

public class ForgeRockNodeListener implements NodeListener<FRSession> {

    private static final String TAG = "ForgeRockNodeListener";
    private final PluginCall call;

    public ForgeRockNodeListener(PluginCall call) {
        this.call = call;
    }

    public PluginCall getCall() {
        return this.call;
    }

    @Override
    public void onSuccess(FRSession session) {
        try {
            SSOToken token = session.getSessionToken();
            JSObject result = new JSObject();
            result.put("token", token.getValue());
            result.put("userExists", true);
            call.resolve(result);
            Log.d(TAG, "Authentication successful. Token: " + token.getValue());
        } catch (Exception e) {
            Log.e(TAG, "Error extracting token", e);
            call.reject("Failed to retrieve token from session: " + e.getMessage(), e);
        }
    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "Authentication error", e);
        if (e instanceof AuthenticationException) {
            call.reject("AuthenticationException: " + e.getMessage(), e);
        } else {
            call.reject("Unknown error during authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public void onCallbackReceived(Node node) {
        try {
            Log.d(TAG, "Node received: " + node.toString());

            String AuthId = node.getAuthId();
            Log.d(TAG, "AuthId: " + AuthId);

            if (AuthId != null) {
                JSObject result = new JSObject();
                result.put("authId", node.getAuthId());

                call.resolve(result);
            }
            else {
                Log.d(TAG, "Node AuthId is null.");
            }
        } catch (Exception e) {
            call.reject("Error handling node: " + e.getMessage(), e);
        }
    }
}
