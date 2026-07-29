import ComposeApp
import AVFAudio

class SwiftWordTtsManager: NSObject, CoreWordTtsManager, AVSpeechSynthesizerDelegate, AVAudioPlayerDelegate {

    private let synthesizer = AVSpeechSynthesizer()
    private var continuation: CheckedContinuation<Void, Never>?

    private var audioPlayer: AVAudioPlayer?
    private var audioPlayerContinuation: CheckedContinuation<Void, Never>?

    private let clipsDirectory: String
    // reading -> pre-generated clip filename, e.g. "ひと-": "w0002.mp3". Built by synthesizing
    // each reading's actual kanji (not the bare reading text) so the TTS engine's own Japanese
    // frontend can pick the correct pitch accent, which isn't reliably derivable from isolated
    // kana alone. See buildSrc/.../word-voice-gen for the generation script.
    private let readingToFile: [String: String]

    init(_ voiceData: CoreWordVoiceData) {
        let indexPath = voiceData.indexFilePath
        clipsDirectory = (indexPath as NSString).deletingLastPathComponent

        var index: [String: String] = [:]
        if let contents = try? String(contentsOfFile: indexPath, encoding: .utf8) {
            for line in contents.split(separator: "\n") {
                let parts = line.split(separator: "\t")
                if parts.count >= 2 {
                    index[String(parts[0])] = String(parts[1])
                }
            }
        }
        readingToFile = index

        super.init()
        synthesizer.delegate = self
        debugLog("SwiftWordTtsManager loaded \(index.count) pre-generated clips from \(indexPath)")
    }

    func speak(word: String) async {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Error activating audio session for word TTS, message[\(error.localizedDescription)]")
        }

        if let fileName = readingToFile[word] {
            let filePath = (clipsDirectory as NSString).appendingPathComponent(fileName)
            if let data = FileManager.default.contents(atPath: filePath) {
                do {
                    try await playMp3(data)
                    return
                } catch {
                    debugLog("speak(\"\(word)\") clip playback failed, falling back: \(error)")
                }
            } else {
                debugLog("speak(\"\(word)\") clip file missing at \(filePath), falling back")
            }
        } else {
            debugLog("speak(\"\(word)\") has no pre-generated clip, falling back to system voice")
        }

        await speakWithSystemVoice(word)
    }

    private func playMp3(_ data: Data) async throws {
        let player = try AVAudioPlayer(data: data)
        player.delegate = self
        audioPlayer = player

        await withCheckedContinuation { (newContinuation: CheckedContinuation<Void, Never>) in
            audioPlayerContinuation = newContinuation
            if !player.play() {
                audioPlayerContinuation?.resume()
                audioPlayerContinuation = nil
            }
        }
    }

    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        audioPlayerContinuation?.resume()
        audioPlayerContinuation = nil
    }

    func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: Error?) {
        audioPlayerContinuation?.resume()
        audioPlayerContinuation = nil
    }

    private func speakWithSystemVoice(_ word: String) async {
        let utterance = AVSpeechUtterance(string: word)
        utterance.voice = Self.bestAvailableJapaneseVoice()

        await withCheckedContinuation { (newContinuation: CheckedContinuation<Void, Never>) in
            continuation = newContinuation
            synthesizer.speak(utterance)
        }
    }

    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didFinish utterance: AVSpeechUtterance) {
        continuation?.resume()
        continuation = nil
    }

    func speechSynthesizer(_ synthesizer: AVSpeechSynthesizer, didCancel utterance: AVSpeechUtterance) {
        continuation?.resume()
        continuation = nil
    }

    func isAvailable() async throws -> KotlinBoolean {
        return KotlinBoolean(bool: true)
    }

    var unavailableMessage: String {
        "No Japanese voice found on this device."
    }

    // AVSpeechSynthesisVoice(language:) always returns the system default voice, which is
    // usually the low-quality "compact" one even when a better Enhanced/Premium Japanese voice
    // has been downloaded (Settings > Accessibility > Spoken Content > Voices > Japanese). Pick
    // the best-quality installed ja-JP voice instead. Only used as a fallback for the rare
    // reading that isn't in the pre-generated clip set.
    private static func bestAvailableJapaneseVoice() -> AVSpeechSynthesisVoice? {
        let japaneseVoices = AVSpeechSynthesisVoice.speechVoices()
            .filter { $0.language == "ja-JP" }
        if #available(iOS 16.0, *) {
            if let premium = japaneseVoices.first(where: { $0.quality == .premium }) {
                return premium
            }
        }
        return japaneseVoices.first { $0.quality == .enhanced }
            ?? AVSpeechSynthesisVoice(language: "ja-JP")
    }

}
