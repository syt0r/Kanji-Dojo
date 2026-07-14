import ComposeApp
import AVFAudio

class SwiftWordTtsManager: NSObject, CoreWordTtsManager, AVSpeechSynthesizerDelegate {

    private let synthesizer = AVSpeechSynthesizer()
    private var continuation: CheckedContinuation<Void, Never>?

    override init() {
        super.init()
        synthesizer.delegate = self
    }

    func speak(word: String) async {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Error activating audio session for word TTS, message[\(error.localizedDescription)]")
        }

        let utterance = AVSpeechUtterance(string: word)
        utterance.voice = AVSpeechSynthesisVoice(language: "ja-JP")

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

    func isAvailable() async -> Bool {
        return AVSpeechSynthesisVoice(language: "ja-JP") != nil
    }

    var unavailableMessage: String {
        "No Japanese voice found on this device."
    }

}
