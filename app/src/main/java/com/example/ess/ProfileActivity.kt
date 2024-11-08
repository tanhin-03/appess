package com.example.ess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale

data class Profile(
    val accountId: Int,
    val fullName: String,
    val emailAddress: String,
    val avatar: String,
    val role: Int,
    val viewArtworks: List<String>,
    val balance: Int
)

class ProfileActivity : BaseActivity() {

//    private lateinit var auth: FirebaseAuth
//    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backArrow: ImageView = findViewById(R.id.backArrow)
        val fullNameTextView: TextView = findViewById(R.id.fullName)
        val emailTextView: TextView = findViewById(R.id.email)
        val cityTextView: TextView = findViewById(R.id.city)
        val logoutButton: Button = findViewById(R.id.logoutButton)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Set the selected item to Profile
        bottomNavigationView.selectedItemId = R.id.navigation_profile

        backArrow.setOnClickListener {
            finish()
        }

        // Fetch user details from Firebase
        val currentUser = auth.currentUser
        currentUser?.let {
            emailTextView.text = it.email

            val userId = it.uid
            database.child("users").child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val userData = snapshot.getValue(UserData::class.java)
                    if (userData != null) {
                        fullNameTextView.text = userData.name
                        cityTextView.text = userData.district
                    }
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    // Handle possible errors
                }
            })
        }

        // Handle logout
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_products -> {
                    val intent = Intent(this@ProfileActivity, ProductActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_promotions -> {
                    val intent = Intent(this@ProfileActivity, PromotionsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    // This is already on the profile page
                    true
                }
                else -> false
            }
        }

        fetchProfileData()
    }

    private fun fetchProfileData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://poserdungeon.myddns.me:5000/profile")
            .addHeader("Authorization", "Bearer ${getTokenFromSession()}")
            .build()
        showProgressDialog(resources.getString(R.string.please_wait))
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                hideProgressDialog()
                runOnUiThread {
                    // Handle failure (e.g., show a Toast)
                    Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                hideProgressDialog()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val profile = Gson().fromJson(it, Profile::class.java)

                        runOnUiThread {
                            // Update the UI with fetched data
                            findViewById<TextView>(R.id.fullName).text = profile.fullName
                            findViewById<TextView>(R.id.email).text = profile.emailAddress
                            findViewById<TextView>(R.id.city).text = String.format(Locale.getDefault(), "Balance: %d", profile.balance)
                        }
                    }
                } else {
                    runOnUiThread {
                        // Handle unsuccessful response (e.g., show a Toast)
                        Toast.makeText(this@ProfileActivity, "Failed to load profile: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getTokenFromSession(): String? {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("authToken", null)
    }

    private fun clearAuthToken() {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        Log.v("LogoutActivity", "Logout Successfully")
        Toast.makeText(this@ProfileActivity, "Logout successfully!", Toast.LENGTH_SHORT).show()
        editor.remove("authToken")  // Removes only the authToken key-value pair
        editor.apply()
    }

    // Data class to match Firebase Realtime Database structure
    data class UserData(
        var name: String = "",
        var district: String = ""
    )
}
