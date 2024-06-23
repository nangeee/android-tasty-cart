package edu.skku.cs.tastycart

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecipeBookmarkActivity : AppCompatActivity() {

    private lateinit var bookmarkListView: ListView
    private lateinit var bookmarkAdapter: RecipeAdapter
    private lateinit var bookmarkList: ArrayList<RecipeActivity.Recipe>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_bookmark)

        bookmarkListView = findViewById(R.id.bookmarkListView)
        bookmarkList = ArrayList()
        bookmarkAdapter = RecipeAdapter(this, bookmarkList)
        bookmarkListView.adapter = bookmarkAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("bookmarks").child(currentUser.uid)
            fetchBookmarkedRecipes()
        }
    }

    private fun fetchBookmarkedRecipes() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookmarkList.clear()
                for (bookmarkSnapshot in snapshot.children) {
                    val label = bookmarkSnapshot.child("label").getValue(String::class.java) ?: ""
                    val image = bookmarkSnapshot.child("image").getValue(String::class.java) ?: ""
                    val url = bookmarkSnapshot.child("url").getValue(String::class.java) ?: ""
                    val ingredientLines = bookmarkSnapshot.child("ingredientLines").getValue(object : GenericTypeIndicator<List<String>>() {})

                    val recipe = RecipeActivity.Recipe(
                        label = label,
                        image = image,
                        url = url,
                        ingredientLines = ingredientLines ?: emptyList()
                    )
                    bookmarkList.add(recipe)
                }
                bookmarkAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Log or handle database read error
            }
        })
    }
}
