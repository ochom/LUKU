package com.lysofts.luku

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken

class Splash : AppCompatActivity() {
    var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler = Handler()
        handler!!.postDelayed({
            if (isLoggedIn) {
                if (isLocationEnable(this@Splash)) {
                    val intent = Intent(this@Splash, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@Splash, LocationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this@Splash, SignUp::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    val isLoggedIn: Boolean
        get() {
            val accessToken = AccessToken.getCurrentAccessToken()
            return accessToken != null
        }

    fun isLocationEnable(context: Context): Boolean {
        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return gps_enabled && network_enabled
    }
}