package com.example.mishop.ui.createOrder.categoryFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mishop.R
import com.example.mishop.ui.Models.DataItemModel
import com.example.mishop.ui.createOrder.categoryFragments.Adapters.adapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_audio.*

class AudioFragment:Fragment(), adapter.ItemClickListener {

    val db = FirebaseFirestore.getInstance()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var audioAdapter: adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_audio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getlist()
        rvProducts.layoutManager = LinearLayoutManager(context)
    }

    private fun getlist() {
        db.collection("Products")
            .whereEqualTo("store_type", "mi_store")
            .whereEqualTo("category", "audio")
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

    }
}