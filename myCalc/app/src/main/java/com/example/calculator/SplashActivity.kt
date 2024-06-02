package com.example.calculator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 3000

    private val THEME_KEY: String = "MyTheme";
    private lateinit var myThemeDataBase: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("isRegistered", false)

        val handler = Handler(Looper.getMainLooper())

        myThemeDataBase = FirebaseDatabase.getInstance().getReference(THEME_KEY)

//        handler.postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }, SPLASH_DISPLAY_LENGTH.toLong())

        chooseTheme("0")

        if (isRegistered) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(
                        this@SplashActivity,
                        AuthenticationActivity::class.java
                    )
                )
                finish()
            }, 2000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(
                        this@SplashActivity,
                        RegistrationActivity::class.java
                    )
                )
                finish()
            }, 2000)
        }
    }

    private fun chooseTheme(_theme_id: String) {
        lateinit var theme: String;
        if (_theme_id == "1") theme = "-NxFshJOUJyAOqPJixq8"
        else if (_theme_id == "2") theme = "-NxFt822nVZD3rqa6HqE"
        else if (_theme_id == "3") theme = "-NxFtcY56_QN_7hqgJTS"
        else if (_theme_id == "0") theme = "-NxKJi4aAxyhK1kfA9nv"

        myThemeDataBase.child(theme).get().addOnSuccessListener { dataSnapshot ->
            val themeInfo = dataSnapshot.value as Map<String, String>
            val text_color = themeInfo["text_color"] as String
            val bg_color = themeInfo["bg_color"] as String
            val num_bg_color = themeInfo["num_bg_color"] as String
            val button_bg_color = themeInfo["button_bg_color"] as String

            setTheme(text_color, bg_color, num_bg_color, button_bg_color)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to read data from Firebase", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTheme(_text_color: String?, _bg_color: String?,
                         _num_bg_color: String?, _button_bg_color: String?) {

        val textColorId = Color.parseColor(_text_color)
        val bgColorId = Color.parseColor(_bg_color)
        val numBgColorId = Color.parseColor(_num_bg_color)
        val buttonBgColorId = Color.parseColor(_button_bg_color)

        window.statusBarColor = bgColorId
        val constraintLayout: ConstraintLayout = findViewById(R.id.splash_main)
        constraintLayout.setBackgroundColor(bgColorId)

        saveSelectedTheme(_text_color.toString(), _bg_color.toString(),
            _num_bg_color.toString(), _button_bg_color.toString())
    }

    private fun saveSelectedTheme(_text_color: String, _bg_color: String,
                                  _num_bg_color: String, _button_bg_color: String) {
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("text_color").setValue(_text_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("bg_color").setValue(_bg_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("num_bg_color").setValue(_num_bg_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("button_bg_color").setValue(_button_bg_color)
    }
}