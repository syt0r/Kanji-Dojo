import SwiftUI
import ComposeApp
import WanaKana
import AVFoundation
import Firebase

@main
struct KanjiDojoApp: App {
    
    let kotlinApplication: IosKotlinApplication
    
    init() {
        FirebaseApp.configure()
        kotlinApplication = IosKotlinApplication(
            logger: SwiftNativeLogger(),
            japaneseUtils: SwiftWanakanaJapaneseUtils(),
            kanaTtsManagerProvider: { voiceData in SwiftTtsKanaManager(voiceData) },
            wordTtsManagerProvider: { SwiftWordTtsManager() },
            backupArchiveHandlerProvider: { SwiftBackupArchiveHandler() }
        )
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    kotlinApplication.notifyDeepLink(url: url.absoluteString)
                }
        }
    }
    
}

class SwiftNativeLogger : CoreNativeLogger {
    
    func logInfo(message: String) {
        NSLog(message)
    }
    
    func logError(message: String) {
        NSLog(message)
        let userInfo = ["message": message]
        let error = NSError(domain: "error",
                            code: 1,
                            userInfo: userInfo)
        Crashlytics.crashlytics().record(error: error)
    }
    
}
