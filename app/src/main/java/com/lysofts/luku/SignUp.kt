package com.lysofts.luku

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.lysofts.luku.SignUp
import java.util.*

class SignUp : AppCompatActivity() {
    var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        FacebookLogin()
    }

    private fun FacebookLogin() {
        callbackManager = CallbackManager.Factory.create()
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                startActivity(Intent(this@SignUp, MainActivity::class.java))
                finish()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun facebookLogin(view: View?) {
        LoginManager.getInstance().logInWithReadPermissions(this@SignUp, Arrays.asList(EMAIL))
    }

    fun signIn(view: View?) {
        finish()
    }

    companion object {
        private const val EMAIL = "email"
    }
}