package com.example.smarthumidity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.smarthumidity.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val btnRegister: Button = binding.btnregis
        btnRegister.setOnClickListener {
            val email = binding.edtEmailregis.text.toString()
            val password = binding.editPassworregis.text.toString()

            if (password.length < 8) {
                Toast.makeText(this, "Password harus terdiri dari minimal 8 karakter", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(email, password)
            }
        }

        val textView5: TextView = binding.tvbtnlogin
        textView5.setOnClickListener {
            navigateToLoginActivity() // Navigasi ke halaman login
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registrasi berhasil, tambahkan kode di sini untuk tindakan setelah registrasi sukses
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    navigateToLoginActivity() // Navigasi ke halaman login setelah registrasi berhasil
                } else {
                    // Registrasi gagal, tambahkan kode di sini untuk menangani kegagalan registrasi
                    Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Menutup RegisterActivity agar tidak dapat kembali ke halaman registrasi
    }
}
