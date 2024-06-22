package edu.skku.cs.tastycart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(this, "Profile update failed: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish() // Close SignupActivity
                    } else {
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
