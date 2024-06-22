package edu.skku.cs.tastycart


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("grocery-items")


    }
}