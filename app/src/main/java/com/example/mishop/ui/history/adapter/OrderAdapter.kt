package com.example.mishop.ui.history.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mishop.R
import com.example.mishop.ui.Models.History

class OrderAdapter(var history: MutableList<History>, var mClickListener: ItemClickListener)
    : RecyclerView.Adapter<OrderAdapter.ViewHolder>(){

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtOrderID = itemView.findViewById<TextView>(R.id.txtOrderID)
        val txtAmount = itemView.findViewById<TextView>(R.id.txtAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.adapter_history_orders, parent, false)
        return ViewHolder(viewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtOrderID.setText("Order ID - " + history[position].order_id)
        holder.txtAmount.setText("Total Amount - " + history[position].total_amount)
        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return history.size
    }

    fun updateList(temp: MutableList<History>) {
        history = temp
        notifyDataSetChanged()
    }

    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}