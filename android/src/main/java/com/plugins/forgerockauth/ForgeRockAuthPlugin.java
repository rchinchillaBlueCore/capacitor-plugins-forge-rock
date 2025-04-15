package com.plugins.forgerockauth;

import android.content.Context;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;
import com.plugins.forgerockauth.ForgeRockNodeListener;


@CapacitorPlugin(name = "ForgeRockAuth")
public class ForgeRockAuthPlugin extends Plugin {

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
            call.reject("Fallo al inicializar el SDK de ForgeRock", e);
        }
    }

    @PluginMethod
    public void authenticate(PluginCall call) {
        try {
            ForgeRockAuth.authenticate(context, new ForgeRockNodeListener(context, call));
        } catch (Exception e) {
            call.reject("Error durante la autenticación", e);
        }
    }
}
