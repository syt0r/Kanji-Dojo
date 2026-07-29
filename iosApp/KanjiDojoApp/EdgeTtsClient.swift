import Foundation

// Appends to Documents/edge_tts_debug.log, retrievable from the iPad's Files app (the app has
// UIFileSharingEnabled) without needing Xcode/Console attached to the device.
func debugLog(_ message: String) {
    guard let documentsUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
        return
    }
    let logUrl = documentsUrl.appendingPathComponent("edge_tts_debug.log")
    let line = "[\(ISO8601DateFormatter().string(from: Date()))] \(message)\n"
    guard let data = line.data(using: .utf8) else { return }
    if let handle = try? FileHandle(forWritingTo: logUrl) {
        defer { try? handle.close() }
        handle.seekToEndOfFile()
        handle.write(data)
    } else {
        try? data.write(to: logUrl)
    }
}
