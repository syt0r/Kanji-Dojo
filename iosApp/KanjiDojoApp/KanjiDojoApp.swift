import SwiftUI
import ComposeApp
import WanaKana
import AVFoundation

@main
struct KanjiDojoApp: App {
    
    let kotlinApplication = IosKotlinApplication(
        japaneseUtils: SwiftWanakanaJapaneseUtils(),
        kanaTtsManagerProvider: { voiceData in SwiftTtsKanaManager(voiceData) },
        backupArchiveHandlerProvider: { SwiftBackupArchiveHandler() }
    )
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    kotlinApplication.notifyDeepLink(url: url.absoluteString)
                }
        }
    }
    
}
