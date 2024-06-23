package edu.skku.cs.tastycart

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecipeAdapter(private val context: Context, private val recipes: List<RecipeActivity.Recipe>) : BaseAdapter() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val databaseReference = FirebaseDatabase.getInstance().getReference("bookmarks").child(currentUser!!.uid)

    override fun getCount(): Int {
        return recipes.size
    }

    override fun getItem(position: Int): RecipeActivity.Recipe {
        return recipes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false)
        val recipe = getItem(position)

        val recipeImageView = view.findViewById<ImageView>(R.id.recipeImageView)
        val recipeNameTextView = view.findViewById<TextView>(R.id.recipeNameTextView)
        val recipeIngredientsTextView = view.findViewById<TextView>(R.id.recipeIngredientsTextView)
        val bookmarkImageView = view.findViewById<ImageView>(R.id.bookmarkImageView)

        recipeNameTextView.text = recipe.label
        recipeIngredientsTextView.text = recipe.ingredientLines.joinToString(", ")
        Glide.with(context).load(recipe.image).into(recipeImageView)

        // 북마크 상태 체크
        databaseReference.child(recipe.label).get().addOnSuccessListener {
            if (it.exists()) {
                bookmarkImageView.setImageResource(R.drawable.ic_heart_filled)
            } else {
                bookmarkImageView.setImageResource(R.drawable.ic_heart_empty)
            }
        }

        // 북마크 클릭 리스너
        bookmarkImageView.setOnClickListener {
            val ref = databaseReference.child(recipe.label)
            ref.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // 이미 북마크 되어 있음 -> 제거
                    ref.removeValue().addOnSuccessListener {
                        bookmarkImageView.setImageResource(R.drawable.ic_heart_empty)
                    }
                } else {
                    // 북마크 추가 (이미지 URL 포함)
                    val bookmarkData = mapOf(
                        "label" to recipe.label,
                        "image" to recipe.image,
                        "url" to recipe.url,
                        "ingredientLines" to recipe.ingredientLines
                    )
                    ref.setValue(bookmarkData).addOnSuccessListener {
                        bookmarkImageView.setImageResource(R.drawable.ic_heart_filled)
                    }
                }
            }
        }

        view.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.url))
            context.startActivity(intent)
        }

        return view
    }

}
