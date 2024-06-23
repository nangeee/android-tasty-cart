package edu.skku.cs.tastycart

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class RecipeActivity : AppCompatActivity() {

    private lateinit var recipesListView: ListView
    private lateinit var recipesAdapter: RecipeAdapter
    private val recipesList = ArrayList<Recipe>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recipesListView = findViewById(R.id.recipesListView)
        recipesAdapter = RecipeAdapter(this, recipesList)
        recipesListView.adapter = recipesAdapter

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(currentUser.uid)
            fetchCartItems()
        }
    }

    private fun fetchCartItems() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ingredients = ArrayList<String>()
                for (cartSnapshot in snapshot.children) {
                    val productName = cartSnapshot.child("productName").value as String
                    ingredients.add(productName)
                }
                if (ingredients.isNotEmpty()) {
                    fetchRecipes(ingredients)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecipeActivity", "Failed to fetch cart items: ${error.message}")
            }
        })
    }

    private fun fetchRecipes(ingredients: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appKey = "04ef79ecce4bdc740db489df0164b60f"
                val appId = "fb0d4928"
                val client = OkHttpClient()

                // 모든 레시피를 합치기 위한 리스트
                val allRecipes = ArrayList<Recipe>()

                for (ingredient in ingredients) {
                    val url = "https://api.edamam.com/search?q=$ingredient&app_id=$appId&app_key=$appKey"
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        val recipes = parseRecipeResponse(responseBody)
                        allRecipes.addAll(recipes)
                    }
                }

                withContext(Dispatchers.Main) {
                    recipesList.clear()
                    recipesList.addAll(allRecipes)
                    recipesAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("RecipeActivity", "Failed to fetch recipes: ${e.message}")
            }
        }
    }

    private fun parseRecipeResponse(response: String): List<Recipe> {
        val gson = Gson()
        val jsonElement = JsonParser.parseString(response)
        val jsonObject = jsonElement.asJsonObject
        val hits = jsonObject.getAsJsonArray("hits")
        val recipes = ArrayList<Recipe>()

        for (hit in hits) {
            val recipeJson = hit.asJsonObject.getAsJsonObject("recipe")
            val recipe = gson.fromJson(recipeJson, Recipe::class.java)
            recipes.add(recipe)
        }

        return recipes
    }

    data class Recipe(
        val label: String,
        val image: String,
        val url: String,
        val ingredientLines: List<String>
    )
}
