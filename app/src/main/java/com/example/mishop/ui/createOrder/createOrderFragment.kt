package com.example.mishop.ui.createOrder

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.mishop.MainActivity
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.example.mishop.ui.createOrder.SearchProduct.searchProduct
import com.example.mishop.ui.createOrder.categoryFragments.*
import com.example.mishop.ui.createOrder.categoryFragments.cart.Cart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.fragment_create_order.*
import kotlin.math.log

class createOrderFragment : Fragment() {

    private lateinit var mref: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_order, container, false)
    }

    //Always start the activity from Smart-Phones
    override fun onStart() {
        super.onStart()
        val rid = view?.findViewById<RadioButton>(R.id.smartPhonesTv)
        val smarphones = SmartPhonesFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.ll, smarphones)
        transaction.commit()
        rid?.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rg.setOnCheckedChangeListener { group, checkedId ->
            checking(checkedId, R.id.smartPhonesTv, SmartPhonesFragment())
            checking(checkedId, R.id.TvTv, TvFragment())
            checking(checkedId, R.id.laptopTv, LaptopsTabletsFragment())
            checking(checkedId, R.id.audioTv, AudioFragment())
        }

        searchView.setOnClickListener {
            startActivity(Intent(context, searchProduct::class.java))
        }

        cartBtn.setOnClickListener {
            startActivity(Intent(requireContext(), Cart::class.java))
        }

        barcode.setOnClickListener {
            val scanner = IntentIntegrator(activity)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }

        getlistCart()
    }


    private fun getlistCart(){
        mref = Firebase.database.getReference("Orders").child(
            SharedPref(requireContext()).getString(Constants.POS_ID)!!)
        mref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var cartSum = 0
                    for (carti in snapshot.children){
                        cartSum += 1
                    }

                //update the badge on the cart
                badge!!.setNumber(cartSum)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun checking(checkedID: Int, originalID: Int, frag: Fragment){
        val rid = view?.findViewById<RadioButton>(originalID)
        if (checkedID == originalID){
            val fragment = frag
            val trans: FragmentTransaction = requireFragmentManager().beginTransaction()
            trans.replace(R.id.ll, fragment)
            trans.commit()
            rid?.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        else{
            rid?.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
    }
}