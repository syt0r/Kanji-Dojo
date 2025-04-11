import Foundation
import ZIPFoundation
import ComposeApp

class SwiftBackupArchiveHandler : BaseIosBackupArchiveHandler {
    
    override func createBackup(
        backupInfoJson: String,
        preferencesJson: String,
        userDataDatabasePath: String,
        userDataDatabaseFileName: String,
        outputZipPath: String
    ) throws {
        let fileManager = FileManager()
        let outputURL = URL(fileURLWithPath: outputZipPath)
        
        let archive = try Archive(url: outputURL, accessMode: .create)
        
        let backupInfoURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("backup_info.json")
        try backupInfoJson.write(to: backupInfoURL, atomically: true, encoding: .utf8)
        try archive.addEntry(with: "backup_info.json", fileURL: backupInfoURL)
        
        let preferencesURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("preferences.json")
        try preferencesJson.write(to: preferencesURL, atomically: true, encoding: .utf8)
        try archive.addEntry(with: "user_preferences.json", fileURL: preferencesURL)
        
        let userDataURL = URL(fileURLWithPath: userDataDatabasePath)
        try archive.addEntry(with: userDataDatabaseFileName, fileURL: userDataURL)
    }
    
    override func readBackupInfoJson(zipPath: String) throws -> String {
        let archiveURL = URL(fileURLWithPath: zipPath)
        let archive = try! Archive(url: archiveURL, accessMode: .read)
        let entry = archive["backup_info.json"]!
        
        var data = Data()
        _ = try archive.extract(entry) { (chunk) in
            data.append(chunk)
        }
        
        return String(data: data, encoding: .utf8) ?? ""
    }
    
    override func unpackBackupTo(zipPath: String, destinationPath: String) throws {
        let archiveURL = URL(fileURLWithPath: zipPath)
        let destinationURL = URL(fileURLWithPath: destinationPath)
        
        let archive = try Archive(url: archiveURL, accessMode: .read)
        
        let fileManager = FileManager()
        for entry in archive {
            let destinationFileURL = destinationURL.appendingPathComponent(entry.path)
            _ = try archive.extract(entry, to: destinationFileURL)
        }
    }
    
}
