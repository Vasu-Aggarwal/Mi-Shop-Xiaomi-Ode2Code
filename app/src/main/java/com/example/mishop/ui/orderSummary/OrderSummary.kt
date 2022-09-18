package com.example.mishop.ui.orderSummary

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.example.mishop.ui.checkout.Checkout
import com.example.mishop.ui.createOrder.categoryFragments.cart.Adapter.CartAdapter
import com.example.mishop.ui.orderSummary.Adapter.orderAdapter
import com.example.mishop.ui.payment.Payment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_order_summary.*
import java.text.DecimalFormat

class OrderSummary : AppCompatActivity() {

    private var details = ArrayList<String>()
    private lateinit var mref: DatabaseReference
    private lateinit var cartProductList: ArrayList<cartItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        supportActionBar?.hide()

        val bundle = intent.extras
        details = bundle?.getStringArrayList("details")!!

        rvView.layoutManager = LinearLayoutManager(this)
        cartProductList = arrayListOf()
        getCartData()
        setdata()
        setBottomSheetPrice()

        btnFinalCheckOut.setOnClickListener {
            val intent = Intent(this, Payment::class.java)
            intent.putExtra("details", details)
            startActivity(intent)
        }
    }

    private fun setdata() {
        val layoutManager = LinearLayoutManager(this)
        rvView!!.layoutManager = layoutManager

        val summaryPrice = findViewById<TextView>(R.id.summaryPrice)
        val finalSummaryPrice = findViewById<TextView>(R.id.finalSummaryPrice)
        if(details.size == 7){
            summaryPrice.text = DecimalFormat.getCurrencyInstance().format(details[6].toInt())
            finalSummaryPrice.text = DecimalFormat.getCurrencyInstance().format(details[6].toInt())
        }
        else{
            summaryPrice.text = DecimalFormat.getCurrencyInstance().format(details[5].toInt())
            finalSummaryPrice.text = DecimalFormat.getCurrencyInstance().format(details[5].toInt())
        }

        finalSummaryPrice.setTextColor(resources.getColor(R.color.orange))
    }

    private fun getCartData() {
        mref = Firebase.database.getReference("Orders").child(
            SharedPref(this).getString(Constants.POS_ID)!!)

        mref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (carti in snapshot.children){
                        val cart = carti.getValue(cartItemModel::class.java)
                        cartProductList.add(cart!!)
                    }
                    rvView.adapter = orderAdapter(cartProductList)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setBottomSheetPrice() {
        BottomSheetBehavior.from(bottomSheetPriceOs).apply {
            peekHeight = 180
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            if(details.size == 7){
                txtPriceOs.text = DecimalFormat.getCurrencyInstance().format(details[6].toInt())
            }
            else{
                txtPriceOs.text = DecimalFormat.getCurrencyInstance().format(details[5].toInt())
            }
        }
    }

}