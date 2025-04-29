import SwiftUI
import ComposeApp
import WanaKana
import AVFoundation
import Firebase

@main
struct KanjiDojoApp: App {
    
    let kotlinApplication = IosKotlinApplication(
        japaneseUtils: SwiftWanakanaJapaneseUtils(),
        kanaTtsManagerProvider: { voiceData in SwiftTtsKanaManager(voiceData) },
        backupArchiveHandlerProvider: { SwiftBackupArchiveHandler() }
    )
    
    init() {
        FirebaseApp.configure()
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
