package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.PluginCall;

import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;

public class ForgeRockNodeListener implements NodeListener<FRSession> {

    private static final String TAG = "ForgeRockNodeListener";
    private final Context context;
    private final PluginCall call;

    public ForgeRockNodeListener(Context context, PluginCall call) {
        this.context = context;
        this.call = call;
    }

    @Override
    public void onSuccess(FRSession session) {
        Log.d(TAG, "Autenticación exitosa");
        call.resolve();
    }

    @Override
    public void onException(Exception e) {
        Log.e(TAG, "Error en la autenticación", e);
        call.reject("Autenticación fallida", e);
    }

    @Override
    public void onCallbackReceived(Node node) {
        Log.d(TAG, "Node recibido: " + node.getStage());
        ForgeRockAuth.handleNodeCallbacks(node);
        node.next(context, this);
    }
}
