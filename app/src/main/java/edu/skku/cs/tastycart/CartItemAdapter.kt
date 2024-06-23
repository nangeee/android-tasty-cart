package edu.skku.cs.tastycart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartItemAdapter(private val context: Context, private val cartList: ArrayList<CartItem>) : BaseAdapter() {

    private lateinit var databaseReference: DatabaseReference

    override fun getCount(): Int {
        return cartList.size
    }

    override fun getItem(position: Int): CartItem {
        return cartList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val itemView = convertView ?: inflater.inflate(R.layout.item_cart, parent, false)
        val cartItem = getItem(position)

        val productNameTextView = itemView.findViewById<TextView>(R.id.productNameTextView)
        val productPriceTextView = itemView.findViewById<TextView>(R.id.productPriceTextView)
        val quantityTextView = itemView.findViewById<TextView>(R.id.quantityTextView)
        val plusButton = itemView.findViewById<TextView>(R.id.plusButton)
        val minusButton = itemView.findViewById<TextView>(R.id.minusButton)
        val deleteButton = itemView.findViewById<TextView>(R.id.deleteButton)

        productNameTextView.text = cartItem.productName
        productPriceTextView.text = "$" + "%.2f".format(cartItem.productPrice)
        quantityTextView.text = cartItem.quantity.toString()

        plusButton.setOnClickListener {
            cartItem.quantity++
            updateCartItem(cartItem)
            notifyDataSetChanged()
        }

        minusButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                updateCartItem(cartItem)
                notifyDataSetChanged()
            }
        }

        deleteButton.setOnClickListener {
            removeCartItem(cartItem)
            cartList.remove(cartItem)
            notifyDataSetChanged()
        }

        return itemView
    }

    private fun updateCartItem(cartItem: CartItem) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(currentUser!!.uid)

        val query = databaseReference.orderByChild("productName").equalTo(cartItem.productName)
        query.get().addOnSuccessListener {
            for (snapshot in it.children) {
                val key = snapshot.key
                if (key != null) {
                    databaseReference.child(key).setValue(cartItem)
                }
            }
        }
    }

    private fun removeCartItem(cartItem: CartItem) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(currentUser!!.uid)

        val query = databaseReference.orderByChild("productName").equalTo(cartItem.productName)
        query.get().addOnSuccessListener {
            for (snapshot in it.children) {
                val key = snapshot.key
                if (key != null) {
                    databaseReference.child(key).removeValue()
                }
            }
        }
    }
}
