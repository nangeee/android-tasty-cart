package edu.skku.cs.tastycart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var quantityTextView: TextView
    private var quantity: Int = 1

    private lateinit var firebaseAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productNameTextView = findViewById(R.id.productNameTextView)
        productPriceTextView = findViewById(R.id.productPriceTextView)
        productImageView = findViewById(R.id.productImageView)
        quantityTextView = findViewById(R.id.quantityTextView)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser

        val productName = intent.getStringExtra("productName") ?: ""
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productImage = intent.getIntExtra("productImage", R.drawable.product_image)

        productNameTextView.text = productName
        productPriceTextView.text = "$" + "%.2f".format(productPrice)
        productImageView.setImageResource(productImage)

        val plusButton = findViewById<TextView>(R.id.plusButton)
        val minusButton = findViewById<TextView>(R.id.minusButton)
        val addToCartButton = findViewById<Button>(R.id.addToCartButton)
        val goToCartButton = findViewById<Button>(R.id.goToCartButton)
        val viewRecommendedRecipesButton = findViewById<Button>(R.id.viewRecommendedRecipesButton)
        val viewBookmarksButton = findViewById<Button>(R.id.viewBookmarksButton)

        plusButton.setOnClickListener {
            quantity++
            quantityTextView.text = quantity.toString()
        }

        minusButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityTextView.text = quantity.toString()
            }
        }

        addToCartButton.setOnClickListener {
            if (currentUser != null) {
                addToCart(currentUser!!.uid, productName, productPrice, quantity)
            } else {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        }

        goToCartButton.setOnClickListener {
            navigateToFragment("cart")
        }

        viewRecommendedRecipesButton.setOnClickListener {
            navigateToFragment("recipe")
        }

        viewBookmarksButton.setOnClickListener {
            navigateToFragment("bookmark")
        }
    // Fragment로 전환한 후에는 Intent를 사용하여 Fragment를 이동하거나 시작할 수 없음
    // Fragment는 Activity의 일부로 관리되므로, FragmentTransaction을 사용하여 Fragment를 전환해야 함
    // ProductDetailActivity에서 다른 Fragment로 이동하려면 MainActivity로 돌아가서 Fragment를 전환해야 함
//        goToCartButton.setOnClickListener {
//            val intent = Intent(this, CartFragment::class.java)
//            startActivity(intent)
//        }

    }

    // MainActivity로 돌아갈 때 새로운 인스턴스를 시작하게 되면서 Activity 전환 간에 불필요한 화면이 쌓임
    // -> 뒤로가기 눌렀을 때 ProductDetailActivity로 바로 돌아오지 않고 빈 화면이 뜸
//    private fun navigateToFragment(fragmentName: String) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("fragment", fragmentName)
//        startActivity(intent)
//    }

    // MainActivity를 호출할 때 플래그를 사용하여 Activity 스택을 정리
    private fun navigateToFragment(fragmentName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", fragmentName)
        // FLAG_ACTIVITY_CLEAR_TOP: 대상 Activity가 스택의 상단에 이미 존재하면 해당 Activity 위의 모든 Activity를 제거하고, 그 Activity를 다시 사용
        // FLAG_ACTIVITY_SINGLE_TOP: Activity가 스택의 맨 위에 이미 있는 경우 해당 Activity의 새로운 인스턴스를 만들지 않고 재사용
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun addToCart(userId: String, productName: String, productPrice: Double, quantity: Int) {
        val database = FirebaseDatabase.getInstance()
        val cartRef = database.getReference("cart").child(userId).push()

        val cartItem = hashMapOf(
            "productName" to productName,
            "productPrice" to productPrice,
            "quantity" to quantity
        )

        cartRef.setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show()
        }
    }
}
