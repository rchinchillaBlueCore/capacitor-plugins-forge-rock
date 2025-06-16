package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FROptions;
import org.forgerock.android.auth.FROptionsBuilder;
import org.forgerock.android.auth.FRUser;
import org.forgerock.android.auth.UserInfo;
import org.json.JSONObject;

public class ForgeRockAuth {

    private static final String TAG = "[ForgeRockAuth]";
    private static FRSession session;

    public static void initialize(Context context, String url, String realm, String journey) {
        Log.d(TAG, "ForgeRock SDK initialize");
        try {

            String bundleId = context.getPackageName() != null ? context.getPackageName() : "com.globalbank.app";

            FROptions options = FROptionsBuilder.build(frOptionsBuilder -> {
                frOptionsBuilder.server(serverBuilder -> {
                    serverBuilder.setUrl(url);
                    serverBuilder.setRealm(realm);
                    serverBuilder.setCookieName("iPlanetDirectoryPro");
                    return null;
                });
                frOptionsBuilder.service(serviceBuilder -> {
                    serviceBuilder.setAuthServiceName(journey);
                    return null;
                });

                frOptionsBuilder.oauth(serviceBuilder -> {
                    serviceBuilder.setOauthClientId("demo_client") ;
                    serviceBuilder.setOauthRedirectUri(bundleId + "://oauth2redirect");
                    serviceBuilder.setOauthScope("openid profile email");
                    return null;
                });

                return null;
            });

            FRAuth.start(context, options);
            Log.d(TAG, "Initialization successful");
        } catch (Exception e) {
            Log.e(TAG, "Error SDK initialization failed", e);
            throw new RuntimeException("Error inicializando ForgeRock SDK", e);
        }
    }

    public static void authenticate(Context context, String journeyName, ForgeRockNodeListener listener) {
        try {
            Log.d(TAG, "Starting authentication with journey: " + journeyName);
            FRSession.authenticate(context, journeyName, listener);
        } catch (Exception e) {
            Log.e(TAG, "Error starting authentication", e);
            listener.getCall().reject("Error starting authentication: " + e.getMessage(), e);
        }
    }

    public static void userInfo(PluginCall call) {
        if (FRUser.getCurrentUser() != null) {
            FRUser.getCurrentUser().getUserInfo(new FRListener<UserInfo>() {
                @Override
                public void onSuccess(UserInfo result) {
                    try {
                        JSONObject raw = result != null ? result.getRaw() : null;
                        if (raw != null) {
                            JSObject jsUserInfo = JSObject.fromJSONObject(raw);
                            Log.d(TAG, "Getting user information: " + jsUserInfo);

                            call.resolve(jsUserInfo);
                        } else {
                            Log.e(TAG, "Getting user information was null or empty");
                            call.reject("userInfo result was null or empty");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user info", e);
                        call.reject("error", e.getMessage(), e);
                    }
                }

                @Override
                public void onException(Exception e) {
                    Log.e(TAG, "getUserInfo Failed", e);
                    call.reject("error", e.getMessage(), e);
                }
            });

        } else {
            Log.e(TAG, "Current user is null. Not logged in or SDK not initialized yet");
        }
    }

    public static void logout(ForgeRockNodeListener listener) {
        try {
            FRUser currentSession = FRUser.getCurrentUser();

            if (currentSession != null) {
                currentSession.logout();
                listener.onSuccess("Session closed successfully.");
            } else {
                listener.onError("No active session to logout.");
            }

        } catch (Exception e) {
            listener.onError("Error closing the session: " + e.getMessage());
        }
    }
}
