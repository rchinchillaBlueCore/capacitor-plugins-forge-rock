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
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
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

            ForgeRockAuth.authenticate(getContext(), journey, new ForgeRockNodeListener(call));
        } catch (Exception e) {
            Log.e(TAG, "Authentication error", e);
            call.reject("Authentication failed: " + e.getMessage(), e);
        }
    }

}
