package com.example.mishop.ui.history.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mishop.R
import com.example.mishop.ui.Models.Products

class DetailedHistoryAdapter(var productsHistory: MutableList<Products>)
    : RecyclerView.Adapter<DetailedHistoryAdapter.ViewHolder>(){

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtProductName = itemView.findViewById<TextView>(R.id.txtProductName)
        val txtProductPrice = itemView.findViewById<TextView>(R.id.txtProductPrice)
        val txtProductQuantity = itemView.findViewById<TextView>(R.id.txtProductQuantity)
        val txtProductCategory = itemView.findViewById<TextView>(R.id.txtProductCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.adapter_detailed_history_orders, parent, false)
        return ViewHolder(viewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProductName.setText("Product Name - " + productsHistory[position].name)
        holder.txtProductPrice.setText("Product Amount - " + productsHistory[position].price)
        holder.txtProductQuantity.setText("Quantity - " + productsHistory[position].quantity)
        holder.txtProductCategory.setText("Category - " + productsHistory[position].category)
    }

    fun updateList(temp: MutableList<Products>) {
        productsHistory = temp
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return productsHistory.size
    }
}