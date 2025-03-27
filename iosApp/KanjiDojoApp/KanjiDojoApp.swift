import SwiftUI
import ComposeApp
import WanaKana

@main
struct KanjiDojoApp: App {
    
    init() {
        
        let dependencies = IosKotlinApplication.Dependencies(
            japaneseUtils: SwiftWanakanaJapaneseUtils()
        )
        
        IosKotlinApplication.shared.initialize(dependencies: dependencies)
        
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
    
}

class SwiftWanakanaJapaneseUtils: CoreJapaneseUtils {
    func isHiragana(_ receiver: unichar) -> Bool {
        WanaKana.isHiragana(charToString(receiver))
    }

    func isKana(_ receiver: unichar) -> Bool {
        WanaKana.isKana(charToString(receiver))
    }

    func isKanji(_ receiver: unichar) -> Bool {
        WanaKana.isKanji(charToString(receiver))
    }

    func isKatakana(_ receiver: unichar) -> Bool {
        WanaKana.isKatakana(charToString(receiver))
    }

    func kanaToRomaji(_ receiver: String) -> String {
        return WanaKana.toRomaji(receiver)!
    }

    func charToString(_ value: unichar) -> String {
        String.init(format: "%C", value)
    }

}
