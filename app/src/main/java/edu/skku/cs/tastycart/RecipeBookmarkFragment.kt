package edu.skku.cs.tastycart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecipeBookmarkFragment : Fragment() {

    private lateinit var bookmarkListView: ListView
    private lateinit var bookmarkAdapter: RecipeAdapter
    private lateinit var bookmarkList: ArrayList<RecipeFragment.Recipe>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_bookmark, container, false)

        bookmarkListView = view.findViewById(R.id.bookmarkListView)
        bookmarkList = ArrayList()
        bookmarkAdapter = RecipeAdapter(requireContext(), bookmarkList)
        bookmarkListView.adapter = bookmarkAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("bookmarks").child(currentUser.uid)
            fetchBookmarkedRecipes()
        }

        return view
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

                    val recipe = RecipeFragment.Recipe(
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
