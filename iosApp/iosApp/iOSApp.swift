import SwiftUI
import AVFoundation

@main
struct iOSApp: App {
    init() {
        requestMicrophonePermission()
    }

    private func requestMicrophonePermission() {
        let session = AVAudioSession.sharedInstance()
        do {
            try session.setCategory(.playAndRecord, mode: .default, options: [.defaultToSpeaker])
        } catch {
            print("No se pudo configurar la sesión de audio: \(error)")
        }

        session.requestRecordPermission { granted in
            DispatchQueue.main.async {
                print(granted ? "Permiso concedido" : "Permiso denegado")
            }
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
