import Capacitor
import FRAuth
import Foundation

@objc public class ForgeRockNodeHandler: NSObject {
    private let call: CAPPluginCall
    private var lastAuthId: String?

    init(call: CAPPluginCall) {
        self.call = call
    }

    // func handle(node: Node) {
    //     do {
    //         print("[ForgeRock] Node received: \(node)")

    //         let username = call.getString("username")
    //         let password = call.getString("password")
    //         self.lastAuthId = node.authId
    //         print("[ForgeRock] AuthId saved: \(self.lastAuthId ?? "nil")")

    //         if let username = username, let password = password {
    //             for callback in node.callbacks {
    //                 if let nameCallback = callback as? NameCallback {
    //                     nameCallback.setValue(username)
    //                 } else if let passwordCallback = callback as? PasswordCallback {
    //                     passwordCallback.setValue(password)
    //                 }
    //             }

    //             node.next { (token, nextNode, error) in
    //                 if let error = error {
    //                     self.onError(error)
    //                 } else if let token = token {
    //                     self.onSuccess(token)
    //                 } else if let nextNode = nextNode {
    //                     self.handle(node: nextNode)
    //                 }
    //             }
    //             return
    //         }

    //         if let authId = self.lastAuthId {
    //             let result: [String: Any] = ["authId": authId]
    //             call.resolve(result)
    //             print("[ForgeRock] AuthId sent to app: \(authId)")
    //         } else {
    //             print("[ForgeRock] Node AuthId is null")
    //             call.reject("AuthId is null in node")
    //         }

    //     } catch {
    //         print("[ForgeRock] Error handling node: \(error)")
    //         call.reject("Error handling node: \(error.localizedDescription)")
    //     }
    // }

    func handle(node: Node) {
        // Check for username/password from JS
        if let username = call.getString("username"),
            let password = call.getString("password")
        {

            for callback in node.callbacks {
                if let nameCallback = callback as? NameCallback {
                    nameCallback.setValue(username)
                } else if let passwordCallback = callback as? PasswordCallback {
                    passwordCallback.setValue(password)
                }
            }

            // Continue to next node
            node.next(node: Node) { token, nextNode, error in
                // if let error = error {
                //     self.call.reject("Authentication failed: \(error.localizedDescription)")
                // } else if let token = token {
                //     self.onSuccess(token: token)
                // } else if let nextNode = nextNode {
                //     self.handle(node: nextNode)
                // } else {
                //     self.call.reject("Unexpected response from ForgeRock node")
                // }
                if let error = error {
                    print("[ForgeRock] Error starting authentication: \(error)")
                    call.reject("Error starting authentication: \(error.localizedDescription)")
                } else if let nextNode = nextNode {
                    print("[ForgeRock] Received node with \(node.callbacks.count) callbacks")
                    print("[ForgeRock] Received node with \(node.callbacks)")
                    // call.resolve(["status": "nodeReceived", "callbacksCount": node.callbacks.count])
                    self.handle(node: nextNode)
                } else if let token = token {
                    print("[ForgeRock] Authentication complete, token received: \(token)")
                    // call.resolve(["status": "authenticated", "token": token])
                    self.onSuccess(token: token)
                } else {
                    print("[ForgeRock] Unexpected state — no token, node, or error.")
                    call.reject("Unexpected authentication result")
                }
            }

        } else {
            // No credentials yet, return node info (authId or similar)
            self.call.resolve([
                "status": "awaitingInput",
                "callbacks": node.callbacks.map { String(describing: type(of: $0)) },
            ])
        }
    }

    func onSuccess(token: Token) {
        self.call.resolve([
            "status": "authenticated",
            "token": token.value,
        ])
    }
}
