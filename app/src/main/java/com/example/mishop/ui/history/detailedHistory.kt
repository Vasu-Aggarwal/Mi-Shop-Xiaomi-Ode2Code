package com.example.mishop.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.History
import com.example.mishop.ui.Models.Products
import com.example.mishop.ui.history.adapter.DetailedHistoryAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detailed_history.*
import kotlinx.android.synthetic.main.fragment_history.*
import java.lang.Exception

class detailedHistory : AppCompatActivity() {

    private val documentDataList: MutableList<History> = ArrayList()
    private lateinit var historyItemModel: MutableList<History>

    private lateinit var productItemModel: MutableList<Products>
    private val productDataList: MutableList<Products> = ArrayList()

    val db = FirebaseFirestore.getInstance()
    private var order_id: String? = null
    var detailedHistoryAdapter: DetailedHistoryAdapter ?= null
    private lateinit var swipe : SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_history)
        supportActionBar?.hide()

        val bundle = intent.extras
        order_id = bundle?.getString("order_id")

        rvProducts.layoutManager = LinearLayoutManager(this)
        getOrders(order_id)

        swipe = findViewById(R.id.swipel)

        var flag = false

        if (!flag)
            Log.d("fl", "onCreate: "+flag)
            swipe.setOnRefreshListener {
                flag = true
                setData()
                getProducts(order_id)
                swipe.isRefreshing = false
            }

    }

    private fun getProducts(order_id: String?) {
        try {
            db.collection("AllOrders").document(
                SharedPref(this)
                    .getString(Constants.STORE_NAME).toString()).collection("Orders")
                .document(order_id!!).collection("Products")
                .get().addOnSuccessListener {
                    productItemModel = it.toObjects(Products::class.java)
                    productDataList.addAll(productItemModel)
                    setAdapter()
                }
        }
        catch (e: Exception){
            Log.d("er", "getProducts: $e")
        }

    }

    private fun setAdapter() {
        if(detailedHistoryAdapter != null){
            detailedHistoryAdapter?.updateList(productDataList)
            rvProducts.adapter = detailedHistoryAdapter
        }
        else {
            detailedHistoryAdapter = DetailedHistoryAdapter(productItemModel)
            rvProducts.adapter = detailedHistoryAdapter
        }

    }

    private fun getOrders(orderId: String?) {
        try {
            db.collection("AllOrders").document(
                SharedPref(this)
                    .getString(Constants.STORE_NAME).toString()).collection("Orders")
                .whereEqualTo("order_id", orderId)
                .get().addOnSuccessListener {
                    historyItemModel = it.toObjects(History::class.java)
                    documentDataList.addAll(historyItemModel)
                }
        }
        catch (e: Exception){
            Log.d("er", "getOrders: $e")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        cardView3.visibility = View.VISIBLE
        txtOrderID.text = "Order ID : " + historyItemModel[0].order_id
        txtAmount.text = "Amount Paid : " + historyItemModel[0].total_amount
        txtOperatorId.text = "Operator ID : " + historyItemModel[0].operator_id
        txtUserName.text = "Customer Name : " + historyItemModel[0].user_name
        txtUserPhone.text = "Customer Mobile : " + historyItemModel[0].user_phone
        txtUserEmail.text = "Customer Email : " + historyItemModel[0].user_email

        if (historyItemModel[0].mode_of_del == "Home Delivery"){
            txtUserAddress.visibility = View.VISIBLE
            txtUserAddress.text = "Address : " + historyItemModel[0].user_address
        }
    }
}