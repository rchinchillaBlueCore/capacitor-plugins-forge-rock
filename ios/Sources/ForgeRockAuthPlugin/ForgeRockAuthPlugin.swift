import Foundation
import Capacitor
import FRAuth

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ForgeRockAuthPlugin)
public class ForgeRockAuthPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ForgeRockAuthPlugin"
    public let jsName = "ForgeRockAuth"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "initialize", returnType: CAPPluginReturnPromise)
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
                  let journey = call.getString("journey") else {
                call.reject("Missing required parameters: url, realm, or journey")
                return
            }

            do {
                let options = FROptions(url: url.absoluteString,
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
}
