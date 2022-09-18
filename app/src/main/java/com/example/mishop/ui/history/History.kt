package com.example.mishop.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.History
import com.example.mishop.ui.history.adapter.OrderAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_history.*
import java.lang.Exception

class History : Fragment(), OrderAdapter.ItemClickListener {

    val db = FirebaseFirestore.getInstance()
    private val documentDataList: MutableList<History> = ArrayList()
    private lateinit var historyItemModel: MutableList<History>
    var OrderAdapter: OrderAdapter?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvOrders.layoutManager = LinearLayoutManager(context)
        getorders()
    }

    private fun getorders() {
        try {
            db.collection("AllOrders").document(
                SharedPref(activity!!)
                .getString(Constants.STORE_NAME).toString()).collection("Orders")
                .get().addOnSuccessListener {
                    historyItemModel = it.toObjects(History::class.java)
                    documentDataList.addAll(historyItemModel)
                    setAdapter()
                }
        }
        catch (e: Exception){
            Log.d("er", "getOrders: "+e)
        }
    }

    private fun setAdapter() {
        if(OrderAdapter != null){
            OrderAdapter?.updateList(documentDataList)
            rvOrders.adapter = OrderAdapter
        }
        else {
            OrderAdapter = OrderAdapter(historyItemModel, this)
            rvOrders.adapter = OrderAdapter
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(activity, detailedHistory::class.java)
        intent.putExtra("order_id", documentDataList[position].order_id)
        startActivity(intent)
    }
}