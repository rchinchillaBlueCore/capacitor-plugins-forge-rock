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
        CAPPluginMethod(name: "userInfo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getAccessToken", returnType: CAPPluginReturnPromise),
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
        
        let bundleId = Bundle.main.bundleIdentifier ?? "com.globalbank.app"

        do {
            let options = FROptions(
                url: url.absoluteString,
                realm: realm,
                cookieName: "iPlanetDirectoryPro",
                authServiceName: journey,
                oauthClientId: "demo_client",
                oauthRedirectUri: "\(bundleId)://oauth2redirect",
                oauthScope: "openid profile email")

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
        
      FRUser.currentUser?.logout()
        /*
         /* Da error en el siguiente escenario: */
         /* Haces la autenticación con un usuario, cierras sesión y cambias el usuario a autenticarte */
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
        } */
}
    
    @objc func userInfo(_ call: CAPPluginCall) {
        
        print("[ForgeRock] Getting user information...")
        guard let user = FRUser.currentUser else {
 
              let errorMsg = "[ForgeRock] Invalid SDK state: No current user for which to request user info"
                call.reject("error", errorMsg, nil)
              return
        }
        user.getUserInfo { (userInfo, error) in
            if let error = error {
              FRLog.e(String(describing: error))
                call.reject("error", error.localizedDescription, error)
            }
            else if let userInfo = userInfo {
              FRLog.i(userInfo.debugDescription)
                print("[ForgeRock] FRUser: ", userInfo.userInfo)
                call.resolve(userInfo.userInfo)
            }
            else {
              let errorMsg = "[ForgeRock] Invalid SDK state: userInfo returned no result"
              FRLog.e(errorMsg)
                call.reject("error", errorMsg, nil)
            }
          }
    }
    
    @objc func getAccessToken(_ call: CAPPluginCall) {

        guard let user = FRUser.currentUser else {
          let errorMsg = "[ForgeRock] Invalid SDK state: No current user for which to request access tokens"
          FRLog.e(errorMsg)
            call.reject("error", errorMsg, nil)
          return
        }

        user.getAccessToken { user, error in
          if let error = error {
            FRLog.e(String(describing: error))
              call.reject("error", error.localizedDescription, error)
          }
          else if let user = user, let accessToken = user.token {
            let encoder = JSONEncoder()

            do {
              let data = try encoder.encode(accessToken)
              let string = String(data: data, encoding: .utf8)
              FRLog.i(string ?? "[ForgeRock] Encoding of token failed")
                call.resolve([
                       "token": string ?? ""
                   ])
            } catch {
                call.reject("Error", "[ForgeRock] Serialization of tokens failed", error)
            }
          }
          else {
            let errorMsg = "[ForgeRock] Invalid SDK state: getAccessToken returned no result"
            FRLog.e(errorMsg)
              call.reject("error", errorMsg, nil)
          }
        }
      }
}
