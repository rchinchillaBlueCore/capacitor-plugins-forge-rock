package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FROptions;
import org.forgerock.android.auth.FROptionsBuilder;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;

public class ForgeRockAuth {

    private static final String TAG = "ForgeRockAuth";

    public static void initialize(Context context, String url, String realm, String journey) {
        try {
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
                return null;
            });

            FRAuth.start(context, options);
            Log.d(TAG, "ForgeRock SDK inicializado");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando ForgeRock SDK", e);
            throw new RuntimeException("Error inicializando ForgeRock SDK", e);
        }
    }

    public static void authenticate(Context context, NodeListener<FRSession> listener) {
        try {
            FRSession.authenticate(context, (String) null, listener);
        } catch (Exception e) {
            Log.e(TAG, "Error en la autenticación", e);
            listener.onException(e);
        }
    }

    public static void handleNodeCallbacks(Node node) {
        Log.d(TAG, "Node recibido: " + node.getStage());
        // Aquí puedes manejar los diferentes tipos de callbacks del nodo
    }
}
