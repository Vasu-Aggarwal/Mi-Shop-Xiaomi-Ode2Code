package com.example.mishop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.databinding.ActivityMainBinding
import com.example.mishop.ui.Models.DataItemModel
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //remove night mode
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        getlist()
    }

    private fun getlist() {
        db.collection("Products")
            .whereEqualTo("store_type", "mi_store")
            .whereEqualTo("category", "smartphone")
            .get().addOnSuccessListener {
                dataItemModel = it.toObjects(DataItemModel::class.java)
            }
    }


    //Barcode scanning result activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                }
                else {
                    var addArray = hashMapOf<String, Any?>()
                    var flag = false
                    for (i in dataItemModel){

                        //if id found then store the details in hashmap
                        if (result.contents == i.id) {
                            flag = true
                            addArray = hashMapOf(
                            "category" to i.category,
                            "id" to i.id,
                            "image" to i.image,
                            "name" to i.name,
                            "store_type" to i.store_type,
                            "quantity" to 1,
                            "price" to i.price
                            )
                        }
                    }

                    if(flag){
                        val realTimeDB = Firebase.database.reference
                        val orderAcc = SharedPref(this).getString(Constants.POS_ID)

                        realTimeDB.child("Orders").child(orderAcc.toString())
                            .child(result.contents)
                            .setValue(addArray)
                        Toast.makeText(this, "Added to the cart", Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(this, "No Product Found", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}