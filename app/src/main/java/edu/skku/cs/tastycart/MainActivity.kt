package edu.skku.cs.tastycart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // 초기 Fragment 및 BottomNavigationView 설정
        if (savedInstanceState == null) {
            val fragmentName = intent.getStringExtra("fragment")
            when (fragmentName) {
                "cart" -> {
                    loadFragment(CartFragment())
                    bottomNavigationView.selectedItemId = R.id.nav_cart
                }
                "recipe" -> {
                    loadFragment(RecipeFragment())
                    bottomNavigationView.selectedItemId = R.id.nav_recipe
                }
                "bookmark" -> {
                    loadFragment(RecipeBookmarkFragment())
                    bottomNavigationView.selectedItemId = R.id.nav_bookmark
                }
                else -> {
                    loadFragment(HomeFragment())
                    bottomNavigationView.selectedItemId = R.id.nav_home
                }
            }
        }
//        if (savedInstanceState == null) {
//            bottomNavigationView.selectedItemId = R.id.nav_home
//        }


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_recipe -> {
                    loadFragment(RecipeFragment())
                    true
                }
                R.id.nav_bookmark -> {
                    loadFragment(RecipeBookmarkFragment())
                    true
                }
                else -> false
            }
        }

    }

    // Intent는 Activity를 전환하는 데 사용되므로,
    // Fragment를 전환하려면 FragmentTransaction을 통해 Fragment를 교체해야 함
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null) // 백스택에 추가하여 뒤로 가기 버튼으로 이전 Fragment로 돌아갈 수 있도록 함
            .commit()
    }
}
