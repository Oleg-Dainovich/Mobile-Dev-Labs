package com.example.calculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ThemeActivity : AppCompatActivity() {
    private lateinit var buttonTheme1: Button;
    private lateinit var buttonTheme2: Button;
    private lateinit var buttonTheme3: Button;
    private lateinit var buttonBack: Button;

    private val THEME_KEY: String = "MyTheme";
    private lateinit var myThemeDataBase: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myThemeDataBase = FirebaseDatabase.getInstance().getReference(THEME_KEY)

        setContentView(R.layout.theme_layout)

        buttonTheme1 = findViewById(R.id.btn_theme_1)
        buttonTheme2 = findViewById(R.id.btn_theme_2)
        buttonTheme3 = findViewById(R.id.btn_theme_3)
        buttonBack = findViewById(R.id.btn_back)

        buttonTheme1.setOnClickListener {
            chooseTheme("1")
        }

        buttonTheme2.setOnClickListener {
            chooseTheme("2")
        }

        buttonTheme3.setOnClickListener {
            chooseTheme("3")
        }

        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        chooseTheme("0")

//        saveNewTheme("0", "#FFFFFFFF", "#001D3D",
//            "#00000000", "#003566")
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
        val constraintLayout: ConstraintLayout = findViewById(R.id.theme_main)
        constraintLayout.setBackgroundColor(bgColorId)
        buttonTheme1.setBackgroundColor(buttonBgColorId)
        buttonTheme1.setTextColor(textColorId)
        buttonTheme2.setBackgroundColor(buttonBgColorId)
        buttonTheme2.setTextColor(textColorId)
        buttonTheme3.setBackgroundColor(buttonBgColorId)
        buttonTheme3.setTextColor(textColorId)
        buttonBack.setBackgroundColor(buttonBgColorId)
        buttonBack.setTextColor(textColorId)

        //Toast.makeText(this, "Theme set", Toast.LENGTH_SHORT).show()

        saveSelectedTheme(_text_color.toString(), _bg_color.toString(),
            _num_bg_color.toString(), _button_bg_color.toString())
    }

    private fun saveSelectedTheme(_text_color: String, _bg_color: String,
                                  _num_bg_color: String, _button_bg_color: String) {
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("text_color").setValue(_text_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("bg_color").setValue(_bg_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("num_bg_color").setValue(_num_bg_color)
        myThemeDataBase.child("-NxKJi4aAxyhK1kfA9nv").child("button_bg_color").setValue(_button_bg_color)

        //Toast.makeText(this, "Theme saved", Toast.LENGTH_SHORT).show()
    }

    private fun saveNewTheme (_theme_id: String, _text_color: String, _bg_color: String,
                              _num_bg_color: String, _button_bg_color: String) {
        var id: String? = myThemeDataBase.getKey();
        var theme_id: String = _theme_id;
        var text_color: String = _text_color;
        var bg_color: String = _bg_color;
        var num_bg_color: String = _num_bg_color;
        var button_bg_color: String = _button_bg_color;

        var newTheme = MyTheme(id, theme_id, text_color, bg_color, num_bg_color, button_bg_color);
        myThemeDataBase.push().setValue(newTheme);
        Toast.makeText(this, "Theme saved", Toast.LENGTH_SHORT).show()
    }
}