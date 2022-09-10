package com.example.mishop.ui.Login

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mishop.MainActivity
import com.example.mishop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class loginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        auth = Firebase.auth
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val password = findViewById<EditText>(R.id.editTextTextPassword)

        email.addTextChangedListener {
            button.isEnabled = !(it.isNullOrEmpty())
        }

        button.setOnClickListener {

            if(email.text.isNullOrEmpty() || password.text.isNullOrEmpty()){
                if(email.text.isNullOrEmpty())
                    email.error = "Enter the email"
                else
                    password.error = "Enter the password"
            }

            else{
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(Intent(this, storedetails::class.java))
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Please enter correct details",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}