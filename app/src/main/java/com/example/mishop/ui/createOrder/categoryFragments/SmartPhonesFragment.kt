package com.example.mishop.ui.createOrder.categoryFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.DataItemModel
import com.example.mishop.ui.Models.cartItemModel
import com.example.mishop.ui.createOrder.categoryFragments.Adapters.adapter
import com.example.mishop.ui.createOrder.categoryFragments.cart.Adapter.CartAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_show_cart.*
import kotlinx.android.synthetic.main.fragment_audio.*
import kotlinx.android.synthetic.main.fragment_create_order.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SmartPhonesFragment : Fragment(), adapter.ItemClickListener{

    val db = FirebaseFirestore.getInstance()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var audioAdapter: adapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_smart_phones, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getlist()
        rvProducts.layoutManager = LinearLayoutManager(context)
    }

    private fun getlist() {
        db.collection("Products")
            .whereEqualTo("store_type", "mi_store")
            .whereEqualTo("category", "smartphone")
            .get().addOnSuccessListener {
                dataItemModel = it.toObjects(DataItemModel::class.java)
                setAdapter()
            }
    }

    private fun setAdapter() {
        rvProducts.layoutManager = GridLayoutManager(context, 3)
        audioAdapter = adapter(dataItemModel, this)
        rvProducts.adapter = audioAdapter
    }

    override fun onItemClick(position: Int) {
        val realTimeDB = Firebase.database.reference
        val orderAcc = SharedPref(requireContext()).getString(Constants.POS_ID)

        val details = hashMapOf(
            "category" to dataItemModel[position].category,
            "id" to dataItemModel[position].id,
            "image" to dataItemModel[position].image,
            "name" to dataItemModel[position].name,
            "store_type" to dataItemModel[position].store_type,
            "quantity" to 1,
            "price" to dataItemModel[position].price
        )

        realTimeDB.child("Orders").child(orderAcc.toString())
            .child(dataItemModel[position].id)
            .setValue(details)
    }
}