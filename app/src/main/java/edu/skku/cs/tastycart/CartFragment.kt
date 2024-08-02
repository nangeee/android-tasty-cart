package edu.skku.cs.tastycart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var cartListView: ListView
    private lateinit var cartListAdapter: CartItemAdapter
    private lateinit var cartList: ArrayList<CartItem>
    private lateinit var totalAmountTextView: TextView
    private lateinit var orderButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        cartListView = view.findViewById(R.id.cartListView)
        cartList = ArrayList()
        cartListAdapter = CartItemAdapter(requireContext(), cartList)
        cartListView.adapter = cartListAdapter

        totalAmountTextView = view.findViewById(R.id.totalAmountTextView)
        orderButton = view.findViewById(R.id.orderButton)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(currentUser!!.uid)

        fetchCartItems()

        orderButton.setOnClickListener {
            placeOrder()
        }

        return view
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
                // Fragment에서는 Activity와 달리 this를 사용하여 Context를 얻지 않고,
                // requireContext(), getContext(), 또는 requireActivity()를 사용해야 함
                // Toast.makeText(this@CartActivity, "Failed to load cart items", Toast.LENGTH_SHORT).show()
                Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
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
            // Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()
            cartList.clear()
            cartListAdapter.notifyDataSetChanged()
            totalAmountTextView.text = "Total: $0.00"
        }.addOnFailureListener {
            // Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "Failed to place order", Toast.LENGTH_SHORT).show()
        }
    }
}
