package com.plugins.forgerockauth;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.PluginCall;

import org.forgerock.android.auth.FRAuth;
import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.FROptions;
import org.forgerock.android.auth.FROptionsBuilder;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.callback.WebAuthnAuthenticationCallback;
import org.forgerock.android.auth.callback.WebAuthnRegistrationCallback;
import org.forgerock.android.auth.webauthn.WebAuthnKeySelector;

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

    public static void enrollBiometrics(Context context, PluginCall call) {
        try {
            Node node = ForgeRockNodeListener.getBiometricNode();
            if (node == null) {
                call.reject("No biometric step in progress.");
                return;
            }

            WebAuthnRegistrationCallback callback = node.getCallback(WebAuthnRegistrationCallback.class);
            if (callback == null) {
                call.reject("WebAuthnRegistrationCallback not found.");
                return;
            }

            String deviceName = call.getString("deviceName", "Android Device");

            callback.register(context, deviceName, node, new FRListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Biometric registration successful");
                    node.next(context, new ForgeRockNodeListener(call, context));
                }

                @Override
                public void onException(Exception e) {
                    Log.e(TAG, "Biometric registration error", e);
                    node.next(context, new ForgeRockNodeListener(call, context));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Biometric enrollment error", e);
            call.reject("Error during biometric enrollment: " + e.getMessage(), e);
        }
    }

    public static void authenticateBiometric(Context context, NodeListener<FRSession> listener) {
        try {
            FRSession session = FRSession.getCurrentSession();
            if (session != null && ForgeRockNodeListener.getBiometricNode() != null) {
                Node biometricNode = ForgeRockNodeListener.getBiometricNode();

                WebAuthnAuthenticationCallback callback =
                        biometricNode.getCallback(WebAuthnAuthenticationCallback.class);

                if (callback != null) {
                    callback.authenticate(context, biometricNode, WebAuthnKeySelector.DEFAULT, new FRListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            // Continuar con el nodo
                            biometricNode.next(context, listener);
                        }

                        @Override
                        public void onException(Exception e) {
                            Log.e("ForgeRockAuth", "Error during biometric authentication", e);
                            if (listener instanceof ForgeRockNodeListener) {
                                ForgeRockNodeListener frListener = (ForgeRockNodeListener) listener;
                                PluginCall call = frListener.getCall(); // Asegúrate de tener un getter para el call si es privado
                                if (call != null) {
                                    call.reject("Error during biometric authentication: " + e.getMessage(), e);
                                }
                            }
                        }
                    });
                } else {
                    Log.e("ForgeRockAuth", "WebAuthnAuthenticationCallback not found.");
                    if (listener instanceof ForgeRockNodeListener) {
                        ForgeRockNodeListener frListener = (ForgeRockNodeListener) listener;
                        PluginCall call = frListener.getCall();
                        if (call != null) {
                            call.reject("Error during biometric authentication: WebAuthnAuthenticationCallback not found.");
                        }
                    }
                }
            } else {
                Log.e("ForgeRockAuth", "No current session or biometric node available.");
                if (listener instanceof ForgeRockNodeListener) {
                    ForgeRockNodeListener frListener = (ForgeRockNodeListener) listener;
                    PluginCall call = frListener.getCall();
                    if (call != null) {
                        call.reject("Error during biometric authentication: No session or biometric node.");
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ForgeRockAuth", "Error in biometric authentication flow", e);
            if (listener instanceof ForgeRockNodeListener) {
                ForgeRockNodeListener frListener = (ForgeRockNodeListener) listener;
                PluginCall call = frListener.getCall();
                if (call != null) {
                    call.reject("Error during biometric authentication: " + e.getMessage(), e);
                }
            }
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
