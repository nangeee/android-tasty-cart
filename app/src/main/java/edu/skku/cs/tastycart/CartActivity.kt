package edu.skku.cs.tastycart

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var cartListView: ListView
    private lateinit var cartListAdapter: CartItemAdapter
    private lateinit var cartList: ArrayList<CartItem>
    private lateinit var totalAmountTextView: TextView
    private lateinit var orderButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartListView = findViewById(R.id.cartListView)
        cartList = ArrayList()
        cartListAdapter = CartItemAdapter(this, cartList)
        cartListView.adapter = cartListAdapter

        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        orderButton = findViewById(R.id.orderButton)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(currentUser!!.uid)

        fetchCartItems()

        orderButton.setOnClickListener {
            placeOrder()
        }
    }

    private fun fetchCartItems() {
        Log.d("CartActivity", "Fetching cart items for user: ${firebaseAuth.currentUser?.uid}")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartList.clear()
                for (cartSnapshot in snapshot.children) {
                    val cartItem = cartSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        cartList.add(cartItem)
                        Log.d("CartActivity", "Item added: ${cartItem.productName} - ${cartItem.quantity}")
                    }
                }
                if (cartList.isEmpty()) {
                    Log.d("CartActivity", "No items in cart")
                }
                cartListAdapter.notifyDataSetChanged()
                calculateTotalAmount()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartActivity", "Failed to fetch cart items: ${error.message}")
                Toast.makeText(this@CartActivity, "Failed to load cart items", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateTotalAmount() {
        var totalAmount = 0.0
        for (item in cartList) {
            totalAmount += item.productPrice * item.quantity
        }
        totalAmountTextView.text = "Total: $" + "%.2f".format(totalAmount)
    }

    private fun placeOrder() {
        databaseReference.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            cartList.clear()
            cartListAdapter.notifyDataSetChanged()
            totalAmountTextView.text = "Total: $0.00"
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
        }
    }
}
