package com.example.smarthumidity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smarthumidity.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CAMERA = 100
        const val REQUEST_GALLERY = 101
        const val CAMERA_PERMISSION_REQUEST = 200
        const val GALLERY_PERMISSION_REQUEST = 201
    }

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_history -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> true // Return true for other cases
            }
        }
        binding.ivProfile.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.btnlogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            Log.d("ProfileActivity", "btnlogout.setOnClickListener called")
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if ((currentUser != null) && (currentUser.email != null)) {
            currentUser.email.also { binding.tvEmail.text = it }
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        val readStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else if (readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestCameraPermission()
            requestGalleryPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_REQUEST
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Izin kamera ditolak, berikan peringatan atau berikan alternatif
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                // Izin akses galeri ditolak, berikan peringatan atau berikan alternatif
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        val imgBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        uploadImage(imgBitmap)
                    }
                }
                REQUEST_GALLERY -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        val imgBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        uploadImage(imgBitmap)
                    }
                }
            }
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance()
            .reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()
        //baos = ByteArrayOutputStream
        ref.putBytes(image).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { urlTask ->
                    if (urlTask.isSuccessful) {
                        val imageUri = urlTask.result
                        Log.d("ProfileActivity", "URL gambar: $imageUri") // Periksa URL gambar di log
                        binding.ivProfile.setImageBitmap(imgBitmap)
                    } else {
                        Log.d("ProfileActivity", "Gagal mendapatkan URL gambar: ${urlTask.exception}")
                    }
                }
            } else {
                Log.d("ProfileActivity", "Gagal mengunggah gambar: ${task.exception}")
            }
        }
    }

}
