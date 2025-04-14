import Foundation

@objc public class ForgeRockAuth: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
