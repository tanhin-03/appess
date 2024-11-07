package com.example.ess

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import kotlin.math.log

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var forgotPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: TextView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize views
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        forgotPassword = findViewById(R.id.forgot_password)
        loginButton = findViewById(R.id.login_btn)
        registerButton = findViewById(R.id.register_btn_now)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Set click listeners
        forgotPassword.setOnClickListener(this)
        loginButton.setOnClickListener(this)
        registerButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.login_btn -> {
                    // Validate login details
                    loginRegisteredUser()
                }
                R.id.register_btn_now -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser() {
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = email.text.toString().trim { it <= ' ' }
            val password = password.text.toString().trim { it <= ' ' }

            // Create JSON object for request body
            val jsonObject = JSONObject()
            jsonObject.put("emailAddress", email)
            jsonObject.put("accountPassword", password)

            val requestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            // Create request
            val request = Request.Builder()
                .url("http://poserdungeon.myddns.me:5000/login")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("LoginActivity", "Login failed: ${e.message}")
                    hideProgressDialog()
                    runOnUiThread {
                        showErrorSnackBar("Login failed: ${e.message}", true)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val token = jsonResponse.getString("token") // Adjust key if needed
                        Log.v("LoginResponse", "Token: ${token}")

                        // Save token to SharedPreferences
                        saveAuthToken(token)

                        runOnUiThread {
                            showErrorSnackBar("You are logged in successfully.", false)
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            showErrorSnackBar("Login failed: ${response.message}", true)
                        }
                    }
                }
            })
        }
    }

    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("authToken", token)
        editor.apply()
    }
}
