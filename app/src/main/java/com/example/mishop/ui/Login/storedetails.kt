package com.example.mishop.ui.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mishop.MainActivity
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import kotlinx.android.synthetic.main.activity_store_details.*

class storedetails : AppCompatActivity() {

    private lateinit var sst: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_details)
        supportActionBar?.hide()

        val store_type = resources.getStringArray(R.array.store_type)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, store_type)
        store_type_dm.setAdapter(arrayAdapter)

        val selectedStoreType = findViewById<AutoCompleteTextView>(R.id.store_type_dm)
        selectedStoreType.addTextChangedListener {
            sst = selectedStoreType.text.toString()
        }

        proceed.setOnClickListener {
            val storeName = findViewById<EditText>(R.id.edtStoreName)
            val posId = findViewById<EditText>(R.id.edtPosId)

            Log.d("details", "DETAILS: ${storeName.text}, ${sst}, ${posId.text}")

            //Store all this information in the local system
            SharedPref(this).setString(Constants.STORE_NAME, storeName.text.toString())
            SharedPref(this).setString(Constants.STORE_TYPE, sst)
            SharedPref(this).setString(Constants.POS_ID, posId.text.toString())

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}