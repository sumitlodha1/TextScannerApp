package com.itsxlord.textscanner

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itsxlord.textscanner.databinding.ActivityMainBinding
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var imageUri: Uri
    private var tts: TextToSpeech? = null
    
    val scanHistory = mutableListOf<ScanItem>()

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            processImage(imageUri)
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            processImage(uri)
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to scan text", Toast.LENGTH_SHORT).show()
        }
    }

    private val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        uri?.let { saveTextToFile(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TextToSpeech(this, this)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HistoryFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_save -> {
                    val textToSave = binding.outputText.text.toString().trim()
                    if (textToSave.isNotEmpty()) {
                        val fileName = "ScannedText_${System.currentTimeMillis()}.txt"
                        createDocumentLauncher.launch(fileName)
                    } else {
                        Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnTranslate.setOnClickListener {
            val text = binding.outputText.text.toString().trim()
            if (text.isNotEmpty()) {
                showLanguageDialog(text)
            } else {
                Toast.makeText(this, "No text to translate", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTTS.setOnClickListener {
            val text = binding.outputText.text.toString().trim()
            if (text.isNotEmpty()) {
                speakText(text)
            } else {
                Toast.makeText(this, "Nothing to speak", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShare.setOnClickListener {
            val textToShare = binding.outputText.text.toString().trim()
            if (textToShare.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, textToShare)
                }
                startActivity(Intent.createChooser(intent, "Share text via"))
            } else {
                Toast.makeText(this, "Nothing to share", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCopy.setOnClickListener {
            val textToCopy = binding.outputText.text.toString().trim()
            if (textToCopy.isNotEmpty()) {
                val clipBoard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Scanned Text", textToCopy)
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            if (binding.outputText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Already empty", Toast.LENGTH_SHORT).show()
            } else {
                binding.outputText.setText("")
                Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchCamera() {
        binding.progressBar.visibility = View.VISIBLE
        imageUri = createImageUri()
        cameraLauncher.launch(imageUri)
    }

    private fun processImage(uri: Uri) {
        try {
            binding.progressBar.visibility = View.VISIBLE
            val image = InputImage.fromFilePath(this, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    binding.progressBar.visibility = View.GONE
                    if (visionText.text.isEmpty()) {
                        Toast.makeText(this, "No text detected in image", Toast.LENGTH_SHORT).show()
                    } else {
                        val text = visionText.text
                        binding.outputText.setText(text)
                        addToHistory(text)
                    }
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error scanning text", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            e.printStackTrace()
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLanguageDialog(textToTranslate: String) {
        val languages = arrayOf("Hindi", "Spanish", "French", "German", "Arabic", "Chinese", "Bengali")
        val languageCodes = arrayOf(
            TranslateLanguage.HINDI,
            TranslateLanguage.SPANISH,
            TranslateLanguage.FRENCH,
            TranslateLanguage.GERMAN,
            TranslateLanguage.ARABIC,
            TranslateLanguage.CHINESE,
            TranslateLanguage.BENGALI
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                translateText(textToTranslate, languageCodes[which])
            }
            .show()
    }

    private fun translateText(text: String, targetLang: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLang)
            .build()
        val translator = Translation.getClient(options)
        
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        binding.progressBar.visibility = View.GONE
                        val currentText = binding.outputText.text.toString()
                        val resultText = "$currentText\n\n-----Translated Text-----\n\n$translatedText"
                        binding.outputText.setText(resultText)
                        translator.close()
                    }
                    .addOnFailureListener {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Translation failed", Toast.LENGTH_SHORT).show()
                        translator.close()
                    }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to download language model", Toast.LENGTH_SHORT).show()
                translator.close()
            }
    }

    private fun addToHistory(text: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateTime = dateFormat.format(Date())
        
        val historyItem = ScanItem(text, dateTime)
        if (!scanHistory.contains(historyItem)) {
             scanHistory.add(0, historyItem)
        }
        
        if (scanHistory.size > 15) {
            scanHistory.removeAt(scanHistory.size - 1)
        }
    }

    private fun createImageUri(): Uri {
        val imageFile = File(filesDir, "camera_img.png")
        return FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)
    }

    private fun saveTextToFile(uri: Uri) {
        try {
            val textToSave = binding.outputText.text.toString()
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(textToSave.toByteArray())
                Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
