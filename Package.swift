// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "ForgerockAuth",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "ForgerockAuth",
            targets: ["ForgeRockAuthPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "ForgeRockAuthPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ForgeRockAuthPlugin"),
        .testTarget(
            name: "ForgeRockAuthPluginTests",
            dependencies: ["ForgeRockAuthPlugin"],
            path: "ios/Tests/ForgeRockAuthPluginTests")
    ]
)