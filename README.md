# TextScannerApp

A modern Android text scanner app built with Kotlin that uses OCR (Optical Character Recognition) to extract text from images and documents. Users can capture images using the camera or select them from the gallery, scan text instantly, copy results, and share extracted content easily.

GitHub Repository: [TextScannerApp](https://github.com/sumitlodha1/TextScannerApp)

---

## ✨ Features

- 📷 Scan text using the device camera
- 🖼️ Import images from gallery
- 🔍 OCR-based text extraction
- 📋 Copy extracted text to clipboard
- 📤 Share scanned text instantly
- ⚡ Fast and lightweight UI
- 🎨 Clean Material Design interface

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Platform:** Android
- **IDE:** Android Studio
- **OCR:** ML Kit / Text Recognition APIs
- **UI:** XML + Android Views

---

## 📱 Screenshots

_Add your screenshots here_

```md
![Home Screen](screenshots/home.png)
![Scanner Screen](screenshots/scanner.png)
![Result Screen](screenshots/result.png)
```

---

## 📂 Project Structure

```bash
TextScannerApp/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│
├── gradle/
├── build.gradle
└── README.md
```

---

## 🔑 Permissions Used

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

---

## 💡 How It Works

1. User selects or captures an image
2. Image is processed using OCR
3. Text is extracted from the image
4. Extracted text is displayed for copying or sharing

---

## 📦 Future Improvements

- 🌐 Multi-language OCR support
- 📄 Export scanned text as PDF
- ☁️ Cloud backup support
- 🧠 AI-powered text formatting
- 🔊 Text-to-speech support

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch

```bash
git checkout -b feature-name
```

3. Commit your changes

```bash
git commit -m "Added new feature"
```

4. Push to your branch

```bash
git push origin feature-name
```

5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👨‍💻 Developer

Made with ❤️ by **Sumit Lodha**
