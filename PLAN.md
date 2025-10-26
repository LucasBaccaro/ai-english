# Plan de Desarrollo - App de Llamada de Voz en Vivo con OpenAI Realtime API

## 🎯 Objetivo
Crear una aplicación KMP (Kotlin Multiplatform) con Compose Multiplatform para iOS y Android que permita conversaciones de voz en tiempo real con IA usando OpenAI Realtime API a través de WebRTC.

## 🏗️ Arquitectura
- **Frontend:** Compose Multiplatform (iOS + Android)
- **WebRTC:** `webrtc-kmp` biblioteca multiplatform
- **Networking:** Ktor Client
- **Serialización:** Kotlinx Serialization
- **Backend:** Sin backend por ahora (API key hardcoded temporalmente)

---

## ✅ COMPLETADO

### Fase 1: Setup de Dependencias y Configuración
**Estado:** ✅ Completado

**Archivos modificados:**
- `gradle/libs.versions.toml` - Agregadas versiones de dependencias
- `build.gradle.kts` (raíz) - Agregados plugins
- `composeApp/build.gradle.kts` - Configuración de dependencias y CocoaPods
- `iosApp/Podfile` - Configuración de CocoaPods

**Dependencias agregadas:**
- ✅ `webrtc-kmp:0.125.0` - WebRTC multiplatform
- ✅ `ktor-client-core:3.0.3` - Cliente HTTP
- ✅ `ktor-client-content-negotiation:3.0.3` - Negociación de contenido
- ✅ `ktor-serialization-kotlinx-json:3.0.3` - Serialización JSON
- ✅ `ktor-client-logging:3.0.3` - Logging
- ✅ `ktor-client-android:3.0.3` - Engine Android
- ✅ `ktor-client-darwin:3.0.3` - Engine iOS
- ✅ `kotlinx-serialization-json:1.8.0` - Serialización
- ✅ `kotlinx-coroutines-core:1.10.2` - Corrutinas
- ✅ `kotlinx-datetime:0.6.1` - Manejo de fechas

**CocoaPods configurado:**
- ✅ Plugin `kotlin("native.cocoapods")` aplicado
- ✅ WebRTC-SDK v125.6422.05 agregado al Podfile
- ✅ `pod install` ejecutado exitosamente

### Fase 2: Modelos de Datos
**Estado:** ✅ Completado

**Archivos creados:**

1. **`composeApp/src/commonMain/kotlin/com/baccaro/ai/domain/models/openai/ClientEvents.kt`**
   - ✅ `SessionUpdateEvent` - Configurar sesión
   - ✅ `ConversationItemCreateEvent` - Crear mensajes
   - ✅ `ResponseCreateEvent` - Solicitar respuesta
   - ✅ `InputAudioBufferAppendEvent` - Enviar audio
   - ✅ `InputAudioBufferCommitEvent` - Confirmar audio
   - ✅ `InputAudioBufferClearEvent` - Limpiar buffer
   - ✅ Modelos de configuración: `SessionConfig`, `AudioConfig`, `TurnDetection`, etc.

2. **`composeApp/src/commonMain/kotlin/com/baccaro/ai/domain/models/openai/ServerEvents.kt`**
   - ✅ `SessionCreatedEvent`, `SessionUpdatedEvent` - Eventos de sesión
   - ✅ `InputAudioBufferSpeechStarted/StoppedEvent` - Detección de voz
   - ✅ `ConversationItem*` - Eventos de conversación
   - ✅ `Response*` - Eventos de respuesta
   - ✅ `ResponseOutputText/AudioDeltaEvent` - Streaming de texto/audio
   - ✅ `ResponseOutputAudioTranscriptDeltaEvent` - Transcripciones
   - ✅ `ResponseFunctionCall*` - Function calling
   - ✅ `ErrorEvent` - Manejo de errores
   - ✅ `RateLimitsUpdatedEvent` - Límites de uso
   - ✅ Modelos de soporte: `SessionInfo`, `ResponseInfo`, `UsageInfo`, etc.

3. **`composeApp/src/commonMain/kotlin/com/baccaro/ai/domain/models/ui/CallModels.kt`**
   - ✅ `Message` - Modelo de mensaje en la conversación
   - ✅ `CallState` - Estado de la llamada
   - ✅ `CallEvent` - Eventos de UI
   - ✅ `ConversationState` - Estado completo de la conversación

**Compilación verificada:**
- ✅ `./gradlew :composeApp:compileCommonMainKotlinMetadata` - BUILD SUCCESSFUL

### Fase 3: WebRTC Integration con webrtc-kmp
**Estado:** ✅ Completado

**Tareas completadas:**
- ✅ Crear `RealtimeWebRTCManager` en commonMain
- ✅ Inicializar `PeerConnection` con configuración adecuada
- ✅ Configurar audio local (micrófono) con `MediaDevices.getUserMedia()`
- ✅ Configurar `DataChannel` para eventos JSON con flows
- ✅ Manejar audio remoto (respuesta de la IA) con `onTrack` flow
- ✅ Implementar observers para eventos WebRTC usando flows
- ✅ Crear `EventParser` para serialización/deserialización de eventos

**Archivos creados:**
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/data/webrtc/RealtimeWebRTCManager.kt` ✅
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/data/webrtc/EventParser.kt` ✅

**Compilación verificada:**
- ✅ `./gradlew :composeApp:compileCommonMainKotlinMetadata` - BUILD SUCCESSFUL

**Detalles de implementación:**
- Usa webrtc-kmp v0.125.0 con API basada en Flows
- `PeerConnection` creada directamente sin factory
- `MediaDevices.getUserMedia()` para obtener audio del micrófono
- Observers implementados con `onIceCandidate`, `onTrack`, `onConnectionStateChange`, etc.
- `DataChannel` con flows `onMessage`, `onOpen`, `onClose` para eventos JSON
- `EventParser` maneja 25+ tipos de eventos del servidor y 6 tipos de eventos del cliente

---

## 🚧 EN PROGRESO

*(Ninguna fase en progreso actualmente)*

---

## 📋 PENDIENTE

### Fase 4: OpenAI Realtime Client
**Estado:** ✅ Completado

**Tareas completadas:**
- ✅ Crear `OpenAIRealtimeClient` en commonMain
- ✅ Implementar generación de tokens (temporal con API key hardcoded)
- ✅ Establecer conexión WebRTC con OpenAI
  - ✅ Crear offer SDP
  - ✅ Enviar a `/v1/realtime` endpoint
  - ✅ Aplicar answer SDP
- ✅ Configurar sesión con VAD automático (server_vad)
- ✅ Implementar `EventHandler` para procesar eventos del servidor
- ✅ Manejar ciclo de vida de la sesión (connect/disconnect)

**Archivos creados:**
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/data/openai/OpenAIRealtimeClient.kt` ✅
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/data/openai/EventHandler.kt` ✅

**Compilación verificada:**
- ✅ `./gradlew :composeApp:compileCommonMainKotlinMetadata` - BUILD SUCCESSFUL

**Detalles de implementación:**
- Cliente HTTP con Ktor para comunicación con OpenAI API
- Flujo completo de conexión WebRTC (offer → POST → answer)
- Configuración de sesión con:
  - Server VAD (Voice Activity Detection) automático
  - Audio PCM16 a 24kHz
  - Transcripciones con Whisper
  - Voz configurable (alloy por defecto)
- EventHandler con callbacks para 20+ tipos de eventos
- Métodos para enviar mensajes de texto y controlar mute
- Manejo de estados de conexión expuestos como StateFlows

### Fase 5: Audio & Permissions
**Estado:** ⏳ Pendiente

**Android (androidMain):**
- [ ] Implementar request de permiso `RECORD_AUDIO`
- [ ] Crear `AndroidAudioManager` si es necesario
- [ ] Actualizar `AndroidManifest.xml` con permisos

**iOS (iosMain):**
- [ ] Agregar `NSMicrophoneUsageDescription` a Info.plist
- [ ] Implementar request de permiso con `AVAudioSession`
- [ ] Crear `IOSAudioManager` si es necesario

**Archivos a crear/modificar:**
- `composeApp/src/androidMain/kotlin/com/baccaro/ai/platform/AudioManager.android.kt`
- `composeApp/src/iosMain/kotlin/com/baccaro/ai/platform/AudioManager.ios.kt`
- `composeApp/src/androidMain/AndroidManifest.xml`
- `iosApp/iosApp/Info.plist`

### Fase 6: UI con Compose Multiplatform
**Estado:** ⏳ Pendiente

**Tareas:**
- [ ] Crear `CallViewModel` con StateFlow
- [ ] Implementar `CallScreen` composable
- [ ] Crear componentes:
  - [ ] `CallHeader` - Estado de conexión
  - [ ] `MessageList` - Lista de transcripciones
  - [ ] `MessageBubble` - Burbuja de mensaje
  - [ ] `CallControls` - Botones de control
  - [ ] `SpeakingIndicator` - Indicador de quien habla
- [ ] Integrar con navegación de la app

**Archivos a crear:**
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/presentation/call/CallViewModel.kt`
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/presentation/call/CallScreen.kt`
- `composeApp/src/commonMain/kotlin/com/baccaro/ai/presentation/call/components/`

### Fase 7: Testing e Integración
**Estado:** ⏳ Pendiente

**Tareas:**
- [ ] Testing en Android:
  - [ ] Compilar y ejecutar app
  - [ ] Probar inicio de llamada
  - [ ] Verificar audio bidireccional
  - [ ] Probar transcripciones en tiempo real
  - [ ] Verificar VAD automático
- [ ] Testing en iOS:
  - [ ] Compilar y ejecutar app
  - [ ] Probar las mismas funcionalidades que Android
- [ ] Manejo de errores:
  - [ ] Conexión perdida
  - [ ] Permisos denegados
  - [ ] Errores de API
- [ ] Optimizaciones de performance

---

## 🔧 Configuración Técnica

### OpenAI Realtime API - Configuración Planeada
```kotlin
SessionConfig(
    type = "realtime",
    model = "gpt-realtime",
    outputModalities = listOf("audio", "text"),
    audio = AudioConfig(
        input = AudioInputConfig(
            format = AudioFormat(type = "audio/pcm", rate = 24000),
            turnDetection = TurnDetection(type = "semantic_vad")
        ),
        output = AudioOutputConfig(
            format = AudioFormat(type = "audio/pcm"),
            voice = "alloy" // o "ash", "ballad", "coral", "echo", "sage", "shimmer", "verse"
        )
    ),
    instructions = "Eres un asistente conversacional amigable. Responde de forma natural como en una llamada telefónica."
)
```

### WebRTC - Endpoints
- **Token efímero:** `POST https://api.openai.com/v1/realtime/client_secrets`
- **SDP Exchange:** `POST https://api.openai.com/v1/realtime/calls`

---

## 📝 Notas Importantes

### Seguridad
⚠️ **API Key Hardcoded:** Actualmente la API key está hardcoded para desarrollo. **NUNCA** publicar la app así.
- **TODO:** Implementar backend simple para generar tokens efímeros
- Opciones: Cloud Function, Vercel, Netlify, servidor propio

### Flujo de la Llamada
1. Usuario presiona "Iniciar Llamada"
2. App solicita permisos de micrófono
3. Se genera token efímero (o se usa API key directa)
4. Se establece conexión WebRTC con OpenAI
5. Se configura sesión con VAD automático
6. Usuario habla → VAD detecta → OpenAI escucha
7. Usuario termina de hablar → VAD detecta silencio → IA responde
8. Audio de IA se reproduce automáticamente
9. Transcripciones aparecen en tiempo real en la UI
10. Ciclo se repite hasta que el usuario finaliza la llamada

### Limitaciones Conocidas
- Duración máxima de sesión: 30 minutos
- VAD solo funciona con audio continuo
- La voz de la IA no se puede cambiar después de que empiece a hablar en una sesión
- WebRTC puede tener problemas en redes muy inestables

---

## 📦 Estructura de Archivos del Proyecto

```
ai-english/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/
│   │   │   └── kotlin/com/baccaro/ai/
│   │   │       ├── domain/
│   │   │       │   └── models/
│   │   │       │       ├── openai/
│   │   │       │       │   ├── ClientEvents.kt ✅
│   │   │       │       │   └── ServerEvents.kt ✅
│   │   │       │       └── ui/
│   │   │       │           └── CallModels.kt ✅
│   │   │       ├── data/
│   │   │       │   ├── webrtc/
│   │   │       │   │   ├── RealtimeWebRTCManager.kt ✅
│   │   │       │   │   └── EventParser.kt ✅
│   │   │       │   └── openai/
│   │   │       │       ├── OpenAIRealtimeClient.kt ✅
│   │   │       │       └── EventHandler.kt ✅
│   │   │       └── presentation/
│   │   │           └── call/
│   │   │               ├── CallViewModel.kt ⏳
│   │   │               ├── CallScreen.kt ⏳
│   │   │               └── components/ ⏳
│   │   ├── androidMain/
│   │   │   └── kotlin/com/baccaro/ai/
│   │   │       └── platform/
│   │   │           └── AudioManager.android.kt ⏳
│   │   └── iosMain/
│   │       └── kotlin/com/baccaro/ai/
│   │           └── platform/
│   │               └── AudioManager.ios.kt ⏳
│   └── build.gradle.kts ✅
├── iosApp/
│   ├── Podfile ✅
│   └── iosApp/
│       └── Info.plist ⏳
├── gradle/
│   └── libs.versions.toml ✅
├── build.gradle.kts ✅
└── PLAN.md ✅ (este archivo)
```

**Leyenda:**
- ✅ Completado
- 🔄 En progreso
- ⏳ Pendiente

---

## 🎯 Próximos Pasos Inmediatos

1. **COMPLETADO:** ~~Implementar `RealtimeWebRTCManager` (Fase 3)~~ ✅
2. **COMPLETADO:** ~~Implementar `OpenAIRealtimeClient` (Fase 4)~~ ✅
3. **SIGUIENTE:** Configurar permisos de audio (Fase 5)
4. **DESPUÉS:** Crear UI con Compose (Fase 6)
5. **FINALMENTE:** Testing completo (Fase 7)

---

*Última actualización: 26 de Octubre, 2025 - Fases 3 y 4 completadas exitosamente*
