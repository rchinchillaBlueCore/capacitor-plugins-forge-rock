package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FROptions;
import org.forgerock.android.auth.FROptionsBuilder;

public class ForgeRockAuth {

    private static final String TAG = "ForgeRockAuth";
    private static FRSession session;

    public static void initialize(Context context, String url, String realm, String journey) {
        Log.d("ForgeRockAuth", "ForgeRock SDK initialize");
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

    public static void authenticate(Context context, String journeyName, ForgeRockNodeListener listener) {
        try {
            Log.d(TAG, "Starting authentication with journey: " + journeyName);
            FRSession.authenticate(context, journeyName, listener);
        } catch (Exception e) {
            Log.e(TAG, "Error starting authentication", e);
            listener.getCall().reject("Error starting authentication: " + e.getMessage(), e);
        }
    }

    public static void logout(ForgeRockNodeListener listener) {
        try {
            FRSession currentSession = FRSession.getCurrentSession();

            if (currentSession != null) {
                currentSession.logout();
                listener.onSuccess("Sesión cerrada exitosamente.");
            } else {
                listener.onError("No hay sesión activa para cerrar.");
            }

        } catch (Exception e) {
            listener.onError("Error al cerrar sesión: " + e.getMessage());
        }
    }
}
