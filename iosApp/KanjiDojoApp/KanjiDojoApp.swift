import SwiftUI
import ComposeApp
import WanaKana
import AVFoundation

@main
struct KanjiDojoApp: App {
    
    let kotlinApplication = IosKotlinApplication(
        japaneseUtils: SwiftWanakanaJapaneseUtils(),
        kanaTtsManagerProvider: { voiceData in SwiftTtsKanaManager(voiceData) }
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

class SwiftTtsKanaManager : CoreKanaTtsManager {
    
    private var filePath: String
    private var clips: [String: CoreKanaCharacterVoiceClipData]
    
    private let audioEngine: AVAudioEngine = AVAudioEngine()
    private let audioPlayer: AVAudioPlayerNode = AVAudioPlayerNode()
    private var audioFile: AVAudioFile?
    
    init(_ kanaVoiceData: CoreKanaVoiceData) {
        filePath = kanaVoiceData.assetFileName
        clips = kanaVoiceData.clips.reduce([String: CoreKanaCharacterVoiceClipData]()) {(dict, person) -> [String: CoreKanaCharacterVoiceClipData] in
            var dict = dict
            dict[person.romaji] = person
            return dict
        }
        
        audioEngine.attach(audioPlayer)
        audioEngine.connect(audioPlayer, to: audioEngine.outputNode, format: nil)
        
        print("Initialized SwiftTtsKanaManager with file at=\(filePath)")
    }
    
    func speak(reading: CoreKanaReading) async {
        do {
            
            let clip = clips[reading.nihonShiki]!
            let clipStart = clip.clipStartSec
            let clipEnd = clip.clipEndSec
            
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
            
            if audioFile == nil {
                audioFile = try AVAudioFile(
                    forReading: NSURL.init(fileURLWithPath: filePath).absoluteURL!
                )
            }
            
            let audioFile = audioFile!
            let audioFormat = audioFile.processingFormat
            
            let startFrame = AVAudioFramePosition(clipStart * audioFormat.sampleRate)
            let frameCount: AVAudioFrameCount
            
            if clipEnd != nil {
                frameCount = AVAudioFrameCount(
                    Double(truncating: clipEnd!) * audioFormat.sampleRate - Double(startFrame)
                )
            }
            else {
                frameCount = AVAudioFrameCount(audioFile.length - startFrame)
            }
            
            try audioEngine.start()
            
            await audioPlayer.scheduleSegment(
                audioFile,
                startingFrame: AVAudioFramePosition(clipStart * audioFormat.sampleRate),
                frameCount: AVAudioFrameCount(frameCount),
                at: nil
            )
            
            audioPlayer.play()
            
        } catch {
            print("Error playing kana[\(reading.nihonShiki)], message[\(error.localizedDescription)]")
        }
        
    }
    
}
