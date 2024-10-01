package com.example.text_to_speech_3

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ScannerActivity : AppCompatActivity() {
//
//    private lateinit var previewView:PreviewView
//    private lateinit var captureButton: Button
//    private lateinit var recognizedTextView: TextView
//    private lateinit var imageCapture: ImageCapture
//    private lateinit var cameraExecutor: ExecutorService
//    private lateinit var databaseHelper: DBHelper

    private lateinit var inputImagebtn:MaterialButton
    private lateinit var recognizedText:MaterialButton
    private lateinit var imageTv:ImageView
    private lateinit var recognizedEditText:EditText
    private lateinit var sendBtn:ImageView

    private companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
    }

    private var imageUri:Uri? = null

    private lateinit var cameraPermission:Array<String>
    private lateinit var storagePermission:Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer:TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_scanner)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        previewView = findViewById(R.id.previewView)
//        captureButton = findViewById(R.id.captureImage)
//
//
//        cameraExecutor = Executors.newSingleThreadExecutor()
//        databaseHelper = DBHelper(this@ScannerActivity)
//
//        startCamera()
//
//        captureButton.setOnClickListener {
//            takePhoto()
//        }

        inputImagebtn = findViewById(R.id.inputImage)
        recognizedText = findViewById(R.id.recognizeText)
        imageTv = findViewById(R.id.imageTv)
        recognizedEditText = findViewById(R.id.recognizedTextEdit)
        sendBtn = findViewById(R.id.sendAI)

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        progressDialog = ProgressDialog(this@ScannerActivity)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setCanceledOnTouchOutside(false)


        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        inputImagebtn.setOnClickListener{
            showInputImageDialog()
        }

        recognizedText.setOnClickListener{
            if (imageUri==null){
                showToast("Pick Image first")
            }
            else{
                recognizeTextFromImage()
            }
        }

        sendBtn.setOnClickListener {
            sendAi(recognizedEditText.text.toString())
        }

    }

    private fun recognizeTextFromImage() {
        progressDialog.setMessage("Preparing Image..")
        progressDialog.show()

        try {
            val inputImage = InputImage.fromFilePath(this@ScannerActivity,imageUri!!)
            progressDialog.setMessage("Recognizing Text...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener {
                    text->
                    progressDialog.dismiss()
                    val recognizedText = text.text

                    recognizedEditText.setText(recognizedText)

                    sendAi(recognizedText)
                }
                .addOnFailureListener {
                    e->
                    progressDialog.dismiss()
                    showToast("Failed to prepare image due to ${e.message}")
                }
        }
        catch (e:Exception){
            showToast("Failed to prepare image due to ${e.message}")
        }
    }


    private fun sendAi(text:String){
//        val resultText = findViewById<EditText>(R.id.recognizedTextEdit)
//        val recognizedText = resultText.text.toString()

        val intent = Intent(this@ScannerActivity,ChatActivity::class.java)
            intent.putExtra("Recognized_Text",text)
            startActivity(intent)
        }

    private fun showInputImageDialog() {
        val popUpMenu = PopupMenu(this@ScannerActivity,inputImagebtn)

        popUpMenu.menu.add(Menu.NONE,1,1,"Camera")
        popUpMenu.menu.add(Menu.NONE,2,2,"Gallery")

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            menuItem->
            val id = menuItem.itemId
            if (id==1){
                if (checkCameraPermission()){
                    pickImageCamera()
                }
                else{
                    requestCameraPermission()
                }
            }
            else if (id==2){
                if (checkStoragePermission()){
                    pickImageGallery()
                }
                else{
                    requestStoragePermission()
                }

            }

            return@setOnMenuItemClickListener true
        }

    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                imageTv.setImageURI(imageUri)
            }
            else{
                showToast("Cancelled!!!")
            }
        }

    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == Activity.RESULT_OK){
                imageTv.setImageURI(imageUri)
            }
            else{
                showToast("Cancelled!!!")
            }
        }

    private fun checkStoragePermission():Boolean{
        return ContextCompat.checkSelfPermission(this@ScannerActivity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    }

    private fun checkCameraPermission():Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(this@ScannerActivity,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(this@ScannerActivity,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return cameraResult && storageResult

    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this@ScannerActivity,storagePermission,
            STORAGE_REQUEST_CODE)
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this@ScannerActivity,cameraPermission,
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted){
                        pickImageCamera()
                    }
                    else{
                        showToast("Camera & Storage permissions are required")
                    }
                }
            }

            STORAGE_REQUEST_CODE->{
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted){
                        pickImageGallery()
                    }
                    else{
                        showToast("Storage permission is required")
                    }
                }

            }
        }
    }

    private fun showToast(message:String){
        Toast.makeText(this@ScannerActivity,message,Toast.LENGTH_LONG).show()
    }
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener(Runnable{
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//            val preview = androidx.camera.core.Preview.Builder().build().also {
//                it.setSurfaceProvider(previewView.surfaceProvider)
//            }
//
//            imageCapture = ImageCapture.Builder().build()
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
//            } catch (exc: Exception) {
//                Log.e("CameraX", "Use case binding failed", exc)
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun takePhoto() {
//        val imageCapture = this.imageCapture?:return
//
//        imageCapture.takePicture(ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
//            override fun onCaptureSuccess(image: ImageProxy) {
//                processImage(image)
//                image.close()
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Log.e("CameraX", "Image capture failed", exception)
//            }
//        })
//    }
//
//    @OptIn(ExperimentalGetImage::class)
//    private fun processImage(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//
//            recognizer.process(image)
//                .addOnSuccessListener { visionText ->
//                    val recognizedText = visionText.text
//                    recognizedTextView.text = recognizedText
//
//                    // Store recognized text in SQLite database
//                    databaseHelper.insertText(recognizedText)
//                }
//                .addOnFailureListener { e ->
//                    Log.e("TextRecognition", "Text recognition failed", e)
//                }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }



}


