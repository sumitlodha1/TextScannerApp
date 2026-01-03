
package com.itsxlord.textscanner

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itsxlord.textscanner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val reqCode = 101

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
            if (intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, reqCode)
            } else{
                Toast.makeText(this, "Please Enable Camera Permissions", Toast.LENGTH_SHORT).show()
            }
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

        if (requestCode==reqCode){
            if (resultCode == Activity.RESULT_OK && data !=null){
                val extraInfo = data?.extras
                val bitmapImage = extraInfo?.get("data") as Bitmap

                imageProcessing(bitmapImage)
            }
        }
    }

    private fun imageProcessing (bitmapImage : Bitmap){
        val image = InputImage.fromBitmap(bitmapImage, 0)
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                binding.outputText.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Somthing Went Wrong.....", Toast.LENGTH_SHORT).show()
            }
    }

}