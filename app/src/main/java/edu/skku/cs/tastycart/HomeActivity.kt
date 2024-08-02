package edu.skku.cs.tastycart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: ArrayList<Product>
    private lateinit var databaseReference: DatabaseReference
    private var isLoading = false
    private var itemCount = 20 // 페이지 당 아이템 수
    private var lastVisibleItemKey: String? = null // 마지막 아이템의 키

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productList = ArrayList()
        productAdapter = ProductAdapter(this, productList)
        productsRecyclerView.adapter = productAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("/products")

        productsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItemPosition + itemCount)) {
                    loadMoreProducts()
                }
            }
        })

        loadMoreProducts() // 초기 로드
    }

    private fun loadMoreProducts() {
        isLoading = true

        val query: Query = if (lastVisibleItemKey == null) {
            databaseReference.orderByKey().limitToFirst(itemCount)
        } else {
            databaseReference.orderByKey().startAfter(lastVisibleItemKey).limitToFirst(itemCount)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val items = snapshot.children.iterator()
                    while (items.hasNext()) {
                        val item = items.next()
                        val product = item.getValue(Product::class.java)
                        product?.let { productList.add(it) }
                        lastVisibleItemKey = item.key
                    }
                    productAdapter.notifyDataSetChanged()
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                // Log or handle database read error
            }
        })
    }
}