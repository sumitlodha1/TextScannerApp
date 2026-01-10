
package com.itsxlord.textscanner

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itsxlord.textscanner.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val reqCode = 101
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCamera.setOnClickListener {

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            imageUri = createImageUri()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivityForResult(intent, reqCode)
        }

        binding.btnCopy.setOnClickListener {
            val clipBoard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("Copied Data", binding.outputText.text)

            clipBoard.setPrimaryClip(clip)
        }

        binding.btnClear.setOnClickListener {
            binding.outputText.text = ""
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==reqCode && resultCode == Activity.RESULT_OK){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imageProcessing(bitmap)
        }

    }

    private fun imageProcessing (bitmapImage : Bitmap){
        val image = InputImage.fromBitmap(bitmapImage, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = StringBuilder()

                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        resultText.append(line.text).append("\n")
                    }
                }

                binding.outputText.text = resultText.toString()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Somthing Went Wrong.....", Toast.LENGTH_SHORT).show()
            }
    }

    fun createImageUri (): Uri{
        val imageFile = File(filesDir, "camera_img.png")
        return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
    }

}