package com.plugins.forgerockauth;

import android.util.Log;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;

@CapacitorPlugin(name = "ForgeRockAuth")
public class ForgeRockAuthPlugin extends Plugin {

    private static final String TAG = "ForgeRockPlugin";

    @PluginMethod
    public void initialize(PluginCall call) {
        String url = call.getString("url");
        String realm = call.getString("realm");
        String journey = call.getString("journey");

        try {
            ForgeRockAuth.initialize(getContext(), url, realm, journey);
            JSObject res = new JSObject();
            res.put("status", "initialized");
            call.resolve(res);
        } catch (Exception e) {
            call.reject("Error inicializando ForgeRock", e);
        }
    }

    @PluginMethod
    public void authenticate(PluginCall call) {
        ForgeRockAuth.authenticate(getContext(), new NodeListener<FRSession>() {
            @Override
            public void onSuccess(FRSession session) {
                JSObject res = new JSObject();
                res.put("success", true);
                call.resolve(res);
            }

            @Override
            public void onException(Exception e) {
                call.reject("Error autenticando", e);
            }

            @Override
            public void onCallbackReceived(Node node) {
                ForgeRockAuth.handleNodeCallbacks(node);
                node.next(getContext(), this);
            }
        });
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        ret.put("message", "¡Hola desde Android!");
        ret.put("numeroAleatorio", (int) (Math.random() * 100));

        call.resolve(ret);
    }
}
