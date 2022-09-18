package com.example.mishop.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.mishop.R
import com.example.mishop.ui.orderSummary.OrderSummary
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_checkout.*

class Checkout : AppCompatActivity(){

    val user = Firebase.auth.currentUser
    private lateinit var custMoc: String    //to the the mod of communication
    private lateinit var custMod: String    //to the the mod of delivery
    private val detailsArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        supportActionBar?.hide()

        //Set operator details
        operatorId.hint = user?.uid

        //Set customer mode of communication option
        val moc = resources.getStringArray(R.array.MOC)
        val arrayAdapterMOC = ArrayAdapter(this, R.layout.dropdown_item, moc)
        mocTt.setAdapter(arrayAdapterMOC)

        val mod = resources.getStringArray(R.array.MOD)
        val arrayAdapterMOD = ArrayAdapter(this, R.layout.dropdown_item, mod)
        modTt.setAdapter(arrayAdapterMOD)

        //Save the mode of communication selected by user
        val mocTt = findViewById<AutoCompleteTextView>(R.id.mocTt)
        mocTt.addTextChangedListener {
            custMoc = mocTt.text.toString()
        }

        val modTt = findViewById<AutoCompleteTextView>(R.id.modTt)
        modTt.addTextChangedListener {
            custMod = modTt.text.toString()
            if (custMod == "Home Delivery"){
                custAddLayout.visibility = View.VISIBLE
            }
            else
                custAddLayout.visibility = View.GONE
        }

        btnProceed.setOnClickListener {
            detailsArray.removeAll(detailsArray) //if back button is pressed then empty the array
            val custName = findViewById<EditText>(R.id.custName)
            val custPhone = findViewById<EditText>(R.id.custPhone)
            val custEmail = findViewById<EditText>(R.id.custEmail)

            detailsArray.add(custName.text.toString())
            detailsArray.add(custPhone.text.toString())
            detailsArray.add(custEmail.text.toString())
            detailsArray.add(custMoc)
            detailsArray.add(custMod)

            if (custMod == "Home Delivery"){
                val custAdd = findViewById<EditText>(R.id.custAdd)
                detailsArray.add(custAdd.text.toString())
            }

            //add the total price got from cart
            detailsArray.add(intent.getStringExtra("pp").toString())

            val intent = Intent(this, OrderSummary::class.java)
            intent.putExtra("details", detailsArray)
            startActivity(intent)
        }
    }
}