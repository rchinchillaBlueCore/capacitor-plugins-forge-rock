import Capacitor
import FRAuth
import Foundation

/// Please read the Capacitor iOS Plugin Development Guide
/// here: https://capacitorjs.com/docs/plugins/ios
@objc(ForgeRockAuthPlugin)
public class ForgeRockAuthPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ForgeRockAuthPlugin"
    public let jsName = "ForgeRockAuth"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "initialize", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "authenticate", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "logout", returnType: CAPPluginReturnPromise),
    ]
    private let implementation = ForgeRockAuth()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }

    @objc func initialize(_ call: CAPPluginCall) {
        guard let urlString = call.getString("url"),
            let url = URL(string: urlString),
            let realm = call.getString("realm"),
            let journey = call.getString("journey")
        else {
            call.reject("Missing required parameters: url, realm, or journey")
            return
        }

        do {
            let options = FROptions(
                url: url.absoluteString,
                realm: realm,
                cookieName: "iPlanetDirectoryPro",
                authServiceName: journey)

            try FRAuth.start(options: options)
            print("[ForgeRock] SDK initialized")
            call.resolve(["status": "success"])

        } catch {
            print("[ForgeRock] Initialization failed: \(error)")
            call.reject("ForgeRock SDK initialization failed", error.localizedDescription)
        }
    }

    @objc func authenticate(_ call: CAPPluginCall) {
        guard let journey = call.getString("journey") else {
            call.reject("Missing required parameter: journey")
            return
        }

        print("[ForgeRock] Starting authentication with journey: \(journey)")
        print("[ForgeRock] Current session: ", FRSession.currentSession)
        print("[ForgeRock] FRUser: ", FRUser.currentUser)

        FRSession.authenticate(authIndexValue: journey) { token, node, error in
            let handler = ForgeRockNodeHandler(call: call)
            if let error = error {
                print("[ForgeRock] Error starting authentication: \(error)")
                call.reject("Error starting authentication: \(error.localizedDescription)")
            } else if let node = node {
                print("[ForgeRock] Received node with \(node.callbacks.count) callbacks")
                print("[ForgeRock] Received node with \(node.callbacks)")
                handler.handle(node: node)
            } else if let token = token {
                print("[ForgeRock] Authentication complete, token received: \(token)")
                handler.onSuccess(token: token)
            } else {
                print("[ForgeRock] Unexpected state — no token, node, or error.")
                call.reject("Unexpected authentication result")
            }
        }
    }

    @objc func logout(_ call: CAPPluginCall) {
        if let currentSession = FRSession.currentSession {
            currentSession.logout()
            print("[ForgeRock] Session closed successfully.")
            call.resolve([
                "status": "success",
                "message": "Session closed successfully.",
            ])
        } else {
            print("[ForgeRock] No active session to logout.")
            call.reject("No active session to logout.")
        }
    }
}
