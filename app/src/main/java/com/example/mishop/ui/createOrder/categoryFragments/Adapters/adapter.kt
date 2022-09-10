package com.example.mishop.ui.createOrder.categoryFragments.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mishop.R
import com.example.mishop.ui.Models.DataItemModel

class adapter(var dataItemmodel: MutableList<DataItemModel>, var mClickListener: ItemClickListener)
    : RecyclerView.Adapter<adapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val img = itemView.findViewById<ImageView>(R.id.img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.adapter_audio,
            parent, false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.setText(dataItemmodel.get(position).name)
        Glide.with(context).load(dataItemmodel.get(position).image).into(holder.img)

        holder.itemView.setOnClickListener{
            mClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dataItemmodel.size
    }

    fun updateList(temp: MutableList<DataItemModel>) {
        dataItemmodel = temp
        notifyDataSetChanged()
    }

    interface ItemClickListener{
        fun onItemClick(position: Int){

        }
    }

}