# 📄 TextScannerApp

A powerful Android OCR application built with **Kotlin** that instantly extracts text from images using **Google ML Kit**. Capture via camera or import from gallery — then copy, translate, listen, save, or share the extracted text in seconds.

---

## ✨ Features

| Feature | Description |
|---|---|
| 📷 Camera Scan | Capture images directly using the device camera |
| 🖼️ Gallery Import | Select existing images from your gallery |
| 🔍 OCR Text Extraction | High-accuracy text recognition for printed & handwritten content |
| 📋 Copy to Clipboard | Instantly copy extracted text with one tap |
| 🌐 Real-time Translation | Translate scanned text into multiple languages |
| 🔊 Text to Speech | Listen to extracted text read aloud |
| 💾 Save Text | Save scanned results for later reference |
| 📤 Share Text | Share extracted content via any app instantly |
| ⚡ Fast & Lightweight | Optimised for performance across all Android devices |
| 🎨 Material Design UI | Clean, intuitive interface following Material Design guidelines |

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Platform:** Android
- **IDE:** Android Studio
- **OCR Engine:** Google ML Kit (Text Recognition API) — on-device, no internet required
- **UI:** XML Layouts + Material Design Components

---

## 📂 Project Structure

```
TextScannerApp/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Kotlin source files
│   │   │   ├── res/          # Layouts, drawables, strings
│   │   │   └── AndroidManifest.xml
│
├── gradle/
├── build.gradle.kts
└── README.md
```

---

## 💡 How It Works

```
1. User captures or selects an image
        ↓
2. Image is passed to Google ML Kit's OCR engine
        ↓
3. Text is extracted on-device (no internet needed)
        ↓
4. User can Copy / Translate / Listen / Save / Share the result
```

---

## 🔑 Permissions Used

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- Android device or emulator running **API 21+**

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/sumitlodha1/TextScannerApp.git

# 2. Open in Android Studio
File → Open → Select the cloned folder

# 3. Let Gradle sync, then run on device or emulator
```

---

## 📦 Planned Improvements

- [ ] Export scanned text as PDF
- [ ] Scan history with search
- [ ] Dark mode support
- [ ] Multi-page document scanning
- [ ] Cloud backup integration

---

## 👨‍💻 Developer

**Sumit Lodha**
- 🔗 GitHub: [@sumitlodha1](https://github.com/sumitlodha1)
- 💼 LinkedIn: [sumitlodha1](https://www.linkedin.com/in/sumitlodha1/)
- 🧠 LeetCode: [sumitlodha1](https://leetcode.com/u/sumitlodha1/)

---

> Built with ❤️ using Kotlin & Google ML Kit
