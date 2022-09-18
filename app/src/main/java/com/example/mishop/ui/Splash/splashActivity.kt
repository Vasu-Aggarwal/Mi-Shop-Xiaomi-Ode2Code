package com.example.mishop.ui.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mishop.MainActivity
import com.example.mishop.R
import com.example.mishop.ui.Login.loginActivity
import com.example.mishop.ui.Login.storedetails
import com.google.firebase.auth.FirebaseAuth

class splashActivity : AppCompatActivity() {

    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //remove night mode

        Handler().postDelayed({
            if(auth.currentUser == null) { //if user not logged in
                val intent = Intent(this, loginActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1200)
    }
}