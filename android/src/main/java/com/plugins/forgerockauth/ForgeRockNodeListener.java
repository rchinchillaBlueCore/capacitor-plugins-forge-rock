package com.plugins.forgerockauth;

import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.FRSession;
import org.forgerock.android.auth.Node;
import org.forgerock.android.auth.NodeListener;
import org.forgerock.android.auth.SSOToken;
import org.forgerock.android.auth.callback.Callback;
import org.forgerock.android.auth.callback.NameCallback;
import org.forgerock.android.auth.callback.PasswordCallback;
import org.forgerock.android.auth.callback.WebAuthnAuthenticationCallback;
import org.forgerock.android.auth.callback.WebAuthnRegistrationCallback;
import org.forgerock.android.auth.exception.ApiException;
import org.forgerock.android.auth.exception.AuthenticationException;
import org.forgerock.android.auth.webauthn.WebAuthnKeySelector;
import org.json.JSONObject;

import java.util.List;

public class ForgeRockNodeListener implements NodeListener<FRSession> {

    private static final String TAG = "ForgeRockNodeListener";
    private final PluginCall call;
    private final Context context;

    private static Node currentNode;

    private String lastAuthId;

    public ForgeRockNodeListener(PluginCall call, Context context) {
        this.call = call;
        this.context = context;
    }

    public PluginCall getCall() {
        return this.call;
    }

    private static Node biometricNode;

    public static void setBiometricNode(Node node) {
        biometricNode = node;
    }

    public static Node getBiometricNode() {
        return biometricNode;
    }

    @Override
    public void onSuccess(FRSession session) {
        try {
            SSOToken token = session.getSessionToken();
            JSObject result = new JSObject();
            result.put("token", token.getValue());
            result.put("userExists", true);
            result.put("authId", lastAuthId);
            call.resolve(result);
            Log.d(TAG, "Authentication successful. Token: " + token.getValue());
        } catch (Exception e) {
            Log.e(TAG, "Error extracting token", e);
            call.reject("Failed to retrieve token from session: " + e.getMessage(), e);
        }
    }

    public void onSuccess(String message) {
        JSObject result = new JSObject();
        result.put("message", message);
        call.resolve(result);
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

            String username = call.getString("username");
            String password = call.getString("password");
            lastAuthId = node.getAuthId();
            Log.d(TAG, "AuthId saved: " + lastAuthId);

            // 1. Manejar WebAuthnRegistrationCallback (registro biométrico)
            WebAuthnRegistrationCallback registrationCallback = node.getCallback(WebAuthnRegistrationCallback.class);
            if (registrationCallback != null) {
                Log.d(TAG, "WebAuthnRegistrationCallback detected.");
                String deviceName = call.getString("deviceName");
                if (deviceName == null || deviceName.isEmpty()) {
                    deviceName = "My Android Device";
                }

                registrationCallback.register(context, deviceName, node, new FRListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d(TAG, "Biometric registration successful. Proceeding to next node.");
                        node.next(context, ForgeRockNodeListener.this);
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "Error during biometric registration", e);
                        call.reject("Error during biometric registration: " + e.getMessage(), e);
                    }
                });
                return;
            }

            // 2. Manejar WebAuthnAuthenticationCallback (autenticación biométrica)
            WebAuthnAuthenticationCallback authCallback = node.getCallback(WebAuthnAuthenticationCallback.class);
            if (authCallback != null) {
                Log.d(TAG, "WebAuthnAuthenticationCallback detected.");
                authCallback.authenticate(context, node, WebAuthnKeySelector.DEFAULT, new FRListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d(TAG, "Biometric authentication successful. Proceeding to next node.");
                        node.next(context, ForgeRockNodeListener.this);
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "Error during biometric authentication", e);
                        call.reject("Error during biometric authentication: " + e.getMessage(), e);
                    }
                });
                return;
            }

            // 3. Si es nodo de autenticación normal con usuario y contraseña
            if (username != null && password != null) {
                for (Callback callback : node.getCallbacks()) {
                    if (callback instanceof NameCallback) {
                        ((NameCallback) callback).setName(username);
                    } else if (callback instanceof PasswordCallback) {
                        ((PasswordCallback) callback).setPassword(password.toCharArray());
                    }
                }
                node.next(context, this);
                return;
            }

            // 4. Si no hay username/password, devolver authId al frontend
            if (lastAuthId != null) {
                JSObject result = new JSObject();
                result.put("authId", lastAuthId);
                call.resolve(result);
                Log.d(TAG, "AuthId sent to app: " + lastAuthId);
            } else {
                Log.d(TAG, "Node AuthId is null.");
                call.reject("AuthId is null in node");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error handling node", e);
            call.reject("Error handling node: " + e.getMessage(), e);
        }
    }

    public void onError(String error) {
        call.reject(error);
    }

    public void onError(Exception e) {
        String errorMessage;

        if (e instanceof ApiException apiEx) {
            errorMessage = extractErrorMessage(apiEx);
        } else {
            errorMessage = e.getMessage();
        }

        call.reject("Autenticación fallida: " + errorMessage);
    }

    private String extractErrorMessage(ApiException apiEx) {
        try {
            String json = apiEx.getError();
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.optString("message", "Error desconocido");
        } catch (Exception ex) {
            return "Error procesando respuesta del servidor";
        }
    }
}
