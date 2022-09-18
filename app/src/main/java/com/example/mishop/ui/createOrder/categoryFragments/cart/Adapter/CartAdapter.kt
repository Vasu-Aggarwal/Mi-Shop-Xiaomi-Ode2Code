package com.example.mishop.ui.createOrder.categoryFragments.cart.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mishop.R
import com.example.mishop.Utilities.Constants
import com.example.mishop.Utilities.SharedPref
import com.example.mishop.ui.Models.cartItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat


class CartAdapter(private val cartList: ArrayList<cartItemModel>, var mClickListener: ItemClickListener)
    : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var mref: DatabaseReference

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImg = itemView.findViewById<ImageView>(R.id.itemImg)
        val ProductName = itemView.findViewById<TextView>(R.id.ProductName)
        val price = itemView.findViewById<TextView>(R.id.price)
        val quantity = itemView.findViewById<TextView>(R.id.quantity)
        val deleteBtn = itemView.findViewById<ImageView>(R.id.deleteBtn)
        val minusBtn = itemView.findViewById<ImageView>(R.id.minusBtn)
        val addBtn = itemView.findViewById<ImageView>(R.id.addBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_cart,
            parent, false)
        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ProductName.text = cartList[position].name
        holder.quantity.text = cartList[position].quantity.toString()

        //To add proper commas according to currency
        val oS = cartList[position].price
        val df = DecimalFormat.getCurrencyInstance().format(oS.toInt())
        holder.price.text = df
        Glide.with(context).load(cartList[position].image).into(holder.itemImg)

        mref = Firebase.database.getReference("Orders")

        //Increase the quantity
        holder.addBtn.setOnClickListener {
            val productId = cartList[position].id
            mref.child(SharedPref(context).getString(Constants.POS_ID)!!)
                .child(productId)
                .child("quantity")
                .setValue(cartList[position].quantity+1)

            holder.quantity.text = cartList[position].quantity.toString()
            cartList.removeAll(cartList)
        }

        //Decrement the quantity
        holder.minusBtn.setOnClickListener {
            val productId = cartList[position].id
            var flag = false

            //if quantity is greater than 1 then decrease by 1
            if(cartList[position].quantity > 1){
                mref.child(SharedPref(context).getString(Constants.POS_ID)!!)
                    .child(productId)
                    .child("quantity")
                    .setValue(cartList[position].quantity-1)

            }

            //if quantity is 1 then delete the product from the cart
            else if(cartList[position].quantity == 1){
                val productId = cartList[position].id
                flag = true

                notifyDataSetChanged()
                notifyItemRemoved(position)
                mref.child(SharedPref(context).getString(Constants.POS_ID)!!)
                    .child(productId)
                    .removeValue()
                cartList.removeAll(cartList)
            }

            //if product exists then only update the quantity text
            if(!flag) {
                holder.quantity.text = cartList[position].quantity.toString()
                cartList.removeAll(cartList)
            }
        }

        //Delete the product from the cart
        holder.deleteBtn.setOnClickListener {

            val productId = cartList[position].id
            cartList.removeAll(cartList)
            notifyDataSetChanged()
            notifyItemRemoved(position)
            mref.child(SharedPref(context).getString(Constants.POS_ID)!!)
                .child(productId)
                .removeValue()
        }

        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    interface ItemClickListener{
        fun onItemClick(position: Int)
    }

}