package edu.skku.cs.tastycart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(private val context: Context, private val cartList: ArrayList<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]
        holder.productNameTextView.text = cartItem.productName
        holder.productPriceTextView.text = "$" + "%.2f".format(cartItem.productPrice)
        holder.quantityTextView.text = cartItem.quantity.toString()

        holder.plusButton.setOnClickListener {
            cartItem.quantity++
            updateCartItem(cartItem)
            notifyDataSetChanged()
        }

        holder.minusButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                updateCartItem(cartItem)
                notifyDataSetChanged()
            }
        }

        holder.deleteButton.setOnClickListener {
            removeCartItem(cartItem)
            cartList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
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

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val plusButton: Button = itemView.findViewById(R.id.plusButton)
        val minusButton: Button = itemView.findViewById(R.id.minusButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}
