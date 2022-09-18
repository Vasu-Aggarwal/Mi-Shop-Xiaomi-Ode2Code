package com.example.mishop.ui.createOrder.categoryFragments.cart

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.example.mishop.ui.checkout.Checkout
import com.example.mishop.ui.createOrder.categoryFragments.cart.Adapter.CartAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_show_cart.*
import java.text.DecimalFormat

class Cart : AppCompatActivity(), CartAdapter.ItemClickListener {

    private lateinit var mref: DatabaseReference
    private lateinit var cartProductList: ArrayList<cartItemModel>
    private lateinit var checkoutprice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_cart)
        supportActionBar?.title = "SHOPPING CART"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.orange)))
        rvShowCart.layoutManager = LinearLayoutManager(this)
        cartProductList = arrayListOf()
        getCartData()
        setdata()
        setBottomSheetPrice()
        btnDone.setOnClickListener {
            Log.d("cprice", "onCreate: "+checkoutprice)
            val intent = Intent(this, Checkout::class.java)
            intent.putExtra("pp", checkoutprice)
            startActivity(intent)
        }
    }

    private fun setdata() {
        val layoutManager = LinearLayoutManager(this)
        rvShowCart!!.layoutManager = layoutManager
    }

    private fun getCartData() {
        mref = Firebase.database.getReference("Orders").child(
            SharedPref(this).getString(Constants.POS_ID)!!)

        mref.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (carti in snapshot.children){
                        val cart = carti.getValue(cartItemModel::class.java)
                        cartProductList.add(cart!!)
                    }

                    rvShowCart.adapter = CartAdapter(cartProductList, this@Cart)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setBottomSheetPrice() {
        BottomSheetBehavior.from(bottomSheetPrice).apply {
            peekHeight = 180

            this.state = BottomSheetBehavior.STATE_COLLAPSED

            mref = Firebase.database.getReference("Orders").child(
                SharedPref(this@Cart).getString(Constants.POS_ID)!!)

            var price = 0
            var quantity = 0
            val pricesArray = ArrayList<Int>()
            mref.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children){
                        price = i.child("price").value.toString().toInt()
                        quantity = i.child("quantity").value.toString().toInt()
                        pricesArray.add(price*quantity)
                        }
                    var tp = 0
                    pricesArray.forEach {
                        tp += it
                    }

                    //To show the commas in total price
                    checkoutprice = tp.toString() //to send the price further for payment
                    val oS = tp.toString()
                    val df = DecimalFormat.getCurrencyInstance().format(oS.toInt())
                    txtPrice.text = df

                    if(pricesArray.size == 0){ //if cart is empty then return back
                        btnDone.isEnabled = false
                        finish()
                    }
                    btnDone.text = "checkout(${pricesArray.size})"
                    pricesArray.removeAll(pricesArray.toSet())
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun onItemClick(position: Int) {

    }
}