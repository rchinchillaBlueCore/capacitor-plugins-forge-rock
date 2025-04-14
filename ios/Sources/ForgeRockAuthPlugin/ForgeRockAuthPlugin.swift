import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ForgeRockAuthPlugin)
public class ForgeRockAuthPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ForgeRockAuthPlugin"
    public let jsName = "ForgeRockAuth"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = ForgeRockAuth()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
