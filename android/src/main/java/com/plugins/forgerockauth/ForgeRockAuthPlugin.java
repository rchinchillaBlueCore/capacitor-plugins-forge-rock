package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;
import com.plugins.forgerockauth.ForgeRockNodeListener;

@CapacitorPlugin(name = "ForgeRockAuth")
public class ForgeRockAuthPlugin extends Plugin {

    private static final String TAG = "ForgeRockAuthPlugin";
    public static Context context;

    @Override
    public void load() {
        super.load();
        context = getContext();
    }

    @PluginMethod
    public void initialize(PluginCall call) {
        String url = call.getString("url");
        String realm = call.getString("realm");
        String journey = call.getString("journey");

        try {
            ForgeRockAuth.initialize(context, url, realm, journey);
            call.resolve();
        } catch (Exception e) {
            call.reject("Fallo al initializer el SDK de ForgeRock", e);
        }
    }
    @PluginMethod
    public void authenticate(PluginCall call) {
        try {
            String journey = call.getString("journey");

            if (journey == null) {
                call.reject("Missing journey name");
                return;
            }

            ForgeRockAuth.authenticate(getContext(),
                    journey, new ForgeRockNodeListener(call,getContext()));
        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            call.reject("Authentication failed: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void enrollBiometrics(PluginCall call) {
        try {
            ForgeRockAuth.enrollBiometrics(getContext(), call);
        } catch (Exception e) {
            Log.e(TAG, "Biometric enrollment failed", e);
            call.reject("Biometric enrollment failed: " + e.getMessage(), e);
        }
    }

    @PluginMethod
    public void authenticateBiometrics(PluginCall call) {
        try {
            ForgeRockAuth.authenticateBiometric(getContext(), new ForgeRockNodeListener(call, getContext()));
        } catch (Exception e) {
            Log.e(TAG, "Biometric authentication error", e);
            call.reject("Biometric authentication failed: " + e.getMessage(), e);
        }
    }


    @PluginMethod
    public void logout(PluginCall call) {
        try {
            ForgeRockAuth.logout(new ForgeRockNodeListener(call, getContext()));
        } catch (Exception e) {
            Log.e(TAG, "Logout error", e);
            call.reject("Logout failed: " + e.getMessage(), e);
        }
    }


}
