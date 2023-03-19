package com.example.pdfproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var uploadButton: Button
    private lateinit var downloadButton: Button

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadPdfToFirebaseStorage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadButton = findViewById(R.id.select_pdf_button)
        downloadButton = findViewById(R.id.download_pdf_button)

        uploadButton.setOnClickListener {
            getContent.launch("application/pdf")
        }

        downloadButton.setOnClickListener {
            downloadPdfFromFirebaseStorage()
        }
    }

    private fun uploadPdfToFirebaseStorage(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().getReference("pdfs")

        val filename = UUID.randomUUID().toString() + ".pdf"

        val uploadTask = storageRef.child(filename).putFile(uri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            Toast.makeText(this, "Upload is $progress% done", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "PDF file uploaded successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error uploading PDF file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadPdfFromFirebaseStorage() {
        val storageRef = FirebaseStorage.getInstance().getReference("pdfs")

        val filename = "name.pdf"

        val localFile = createTempFile("temp", "pdf")
        val downloadTask = storageRef.child(filename).getFile(localFile)

        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            Toast.makeText(this, "Download is $progress% done", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "PDF file downloaded successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error downloading PDF file", Toast.LENGTH_SHORT).show()
        }
    }
}

