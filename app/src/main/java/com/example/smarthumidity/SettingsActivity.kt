package com.example.smarthumidity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.smarthumidity.settings.AboutActivity
import com.example.smarthumidity.settings.NotifActivity
import com.example.smarthumidity.settings.PersonalActivity
import com.example.smarthumidity.settings.SecurityActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ivPersonal = findViewById<ImageView>(R.id.ivpersonal)
        val ivNotification = findViewById<ImageView>(R.id.ivnotif)
        val ivAccount = findViewById<ImageView>(R.id.ivaccount)
        val ivAboutApp = findViewById<ImageView>(R.id.ivaboutapp)

        ivPersonal.setOnClickListener {
            val intent = Intent(this, PersonalActivity::class.java)
            startActivity(intent)
        }

        ivNotification.setOnClickListener {
            val intent = Intent(this, NotifActivity::class.java)
            startActivity(intent)
        }

        ivAccount.setOnClickListener {
            val intent = Intent(this, SecurityActivity::class.java)
            startActivity(intent)
        }

        ivAboutApp.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            // Handle bottom navigation clicks
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
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
    }
}
