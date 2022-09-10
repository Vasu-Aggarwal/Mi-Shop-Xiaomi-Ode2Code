package com.example.mishop.ui.createOrder.SearchProduct

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.mishop.R
import com.example.mishop.ui.Models.DataItemModel
import com.example.mishop.ui.createOrder.categoryFragments.Adapters.adapter
import com.google.firebase.firestore.FirebaseFirestore

class searchProduct: AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var list: adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)
        supportActionBar?.hide()
        getlist()
        filterlist()
    }

    private fun filterlist() {

        val listView = findViewById<ListView>(R.id.lvCities)
        val searchView = findViewById<SearchView>(R.id.searchView)

        val temp: MutableList<DataItemModel> = ArrayList()
        for(data in dataItemModel){
            if(data.name.contains(toString().capitalize())
                || data.name.contains(toString().lowercase())){  //user can type in lower as well as upper case
                temp.add(data)
            }
        }
        list.updateList(temp)

        val adapter: ArrayAdapter<DataItemModel> = ArrayAdapter(this, android.R.layout.simple_list_item_1, temp)
        listView.adapter = adapter

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        listView.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "you clicked $position", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getlist() {
        db.collection("Products")
            .whereEqualTo("store_type", "mi_store")
            .whereEqualTo("category", "audio")
            .get().addOnSuccessListener {
                dataItemModel = it.toObjects(DataItemModel::class.java)
            }
    }
}