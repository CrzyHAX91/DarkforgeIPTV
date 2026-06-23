# Baddbeatz Media - Enterprise Streaming App

Baddbeatz Media is a premium, secure streaming environment designed for Fire OS and Android TV devices. It provides a highly optimized, completely secure, and visually stunning interface for managing and playing your personal, legal media libraries.

## 🌟 Key Features

* **Zero-Trust Metadata Enclave (ZTME)**: Stores watch history and preferences in a hardware-backed encrypted enclave. Complete zero-knowledge local autonomy.
* **Edge-Federated QoS Mesh**: On-device AI model analyzes local frame-drops and buffer latency to auto-negotiate stream codecs without sending telemetry to our servers.
* **Stream Health Score Diagnostics**: Built-in enterprise-grade network diagnostic tool measuring ping, path latency, packet loss, and jitter.
* **Cinematic Ambient Hub**: Auto-launches an OLED burn-in defender with subtle, drifting visuals when a stream is paused.
* **AI Audio Whispersync (Night Mode)**: Intelligently compresses dynamic range based on context, clarifying dialogue while dampening loud action scenes.
* **Privacy Shield Core**: End-to-end encrypted metadata transits and completely secure local connections.
* **Kodi IPC Bridge**: Deep linking and secure RPC connectivity directly with Kodi for managing legal offline libraries.
* **Stunning UI/UX**: Built with Jetpack Compose using Material 3, custom shaders, glassmorphism, and a deeply optimized OLED-friendly neon aesthetic.
* **Legal Offline Library Manager**: A robust offline downloading hub strictly enforcing secure HTTPS allowlisted domains for DRM-free personal media handling.

## 🛠 Setup & Installation

### Requirements

* Android Studio Ladybug (or newer)
* Minimum SDK 26 (Android 8.0)
* Target SDK 34 (Android 14)

### Building the App

1. Clone or download the repository to your local machine.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Select `app` from the run configuration menu.
5. Click the **Run** button or press `Shift + F10` to deploy to your connected Fire TV device or Android emulator.

### Configuration

If you intend to connect Baddbeatz Media to your own personal backend or an IPTV provider (like Xtream API), configure the endpoint via the **Settings -> Xtream Login** directly in the app's user interface. 

The **Privacy Settings** dashboard allows you to toggle maximum security options such as the telemetry shield and metadata encryption keys.

## 📱 Supported Devices

Optimized explicitly for 10-foot UI experiences like:
* Amazon Fire TV Stick 4K / Cube
* NVIDIA Shield TV
* Chromecast with Google TV
* Standard Android TV sets

## ⚖️ Legal & Compliance

This application is strictly designed exclusively for playing media content that you have the legal right to access. It does NOT provide, host, or scrape any media. It is not an IPTV list indexer. Use of the Legal Offline Library is locked to secure, private domains you manage.

---
*Built with ❤️ utilizing Jetpack Compose, Kotlin, and modern Edge telemetry architectures.*
