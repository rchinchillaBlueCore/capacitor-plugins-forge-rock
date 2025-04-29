import Capacitor
import FRAuth
import Foundation

@objc public class ForgeRockNodeHandler: NSObject {
    private let call: CAPPluginCall
    private var lastAuthId: String?

    init(call: CAPPluginCall) {
        self.call = call
    }

    func handle(node: Node) {
        // // Check for username/password from JS
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
            node.next { (user: FRUser?, nextNode, error) in
                if let error = error {
                    print("[ForgeRock] Error starting authentication: \(error)")
                    self.call.reject("Error starting authentication: \(error.localizedDescription)")
                } else if let user = user {
                    print("[ForgeRock] Authentication complete, token received: \(user.token)")
                    self.onSuccess(token: user.token)
                } else if let nextNode = nextNode {
                    self.handle(node: nextNode)
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

    func onSuccess(token: Token?) {
        if let token = token {
            self.call.resolve([
                "status": "authenticated",
                "token": token.value,
            ])
        } else {
            print("[ForgeRock] Unexpected state — no token, node, or error.")
            self.call.reject("Unexpected authentication result")
        }

    }
}
