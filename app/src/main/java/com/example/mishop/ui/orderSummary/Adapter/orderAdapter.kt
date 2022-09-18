package com.example.mishop.ui.orderSummary.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat

class orderAdapter(private val cartList: ArrayList<cartItemModel>)
    : RecyclerView.Adapter<orderAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var mref: DatabaseReference

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg = itemView.findViewById<ImageView>(R.id.itemImg)
        val ProductName = itemView.findViewById<TextView>(R.id.ProductName)
        val price = itemView.findViewById<TextView>(R.id.price)
        val quantity = itemView.findViewById<TextView>(R.id.quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_order_summary,
            parent, false)
        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ProductName.text = cartList[position].name
        holder.quantity.text = "X"+cartList[position].quantity.toString()

        //To add proper commas according to currency
        val oS = cartList[position].price
        val df = DecimalFormat.getCurrencyInstance().format(oS.toInt())
        holder.price.text = df
        Glide.with(context).load(cartList[position].image).into(holder.itemImg)

        mref = Firebase.database.getReference("Orders")

    }

}