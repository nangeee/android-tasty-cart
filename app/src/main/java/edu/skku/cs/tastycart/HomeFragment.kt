package edu.skku.cs.tastycart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: ArrayList<Product>
    private lateinit var databaseReference: DatabaseReference
    private var isLoading = false
    private var itemCount = 20 // 페이지 당 아이템 수
    private var lastVisibleItemKey: String? = null // 마지막 아이템의 키

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // LayoutInflater를 사용하여 Fragment의 레이아웃을 인플레이트
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // RecyclerView 및 어댑터 설정
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        productList = ArrayList()
        // Context 사용 변경: Fragment에서는 requireContext()를 사용하여 Context를 얻음
        // productAdapter = ProductAdapter(this, productList)
        productAdapter = ProductAdapter(requireContext(), productList)
        productsRecyclerView.adapter = productAdapter

        // Firebase Database 레퍼런스 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("/products")

        // 스크롤 리스너 추가
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

        // 초기 데이터 로드
        loadMoreProducts()

        return view
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
