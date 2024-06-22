package edu.skku.cs.tastycart

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = ArrayList()
        productAdapter = ProductAdapter(this, productList)
        productsRecyclerView.adapter = productAdapter

        fetchProductsFromFirebase()
    }

    private fun fetchProductsFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("/products")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("HomeActivity", "Failed to read value.", error.toException())
            }
        })
    }
}
