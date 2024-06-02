package com.example.calculator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReadActivity : AppCompatActivity() {
    private lateinit var historyTextView: TextView;
    private val NOTIFICATION_KEY: String = "MyNotification";
    private val THEME_KEY: String = "MyTheme";
    private lateinit var myDataBase: DatabaseReference;
    private lateinit var myThemeDataBase: DatabaseReference;
    private lateinit var buttonBack: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.read_layout)
        myDataBase = FirebaseDatabase.getInstance().getReference(NOTIFICATION_KEY)
        myThemeDataBase = FirebaseDatabase.getInstance().getReference(THEME_KEY)
        historyTextView = findViewById(R.id.history_text_view)

        buttonBack = findViewById(R.id.btn_back)

        getDataFromDB()

        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        chooseTheme("0")
    }

    private fun getDataFromDB() {
        myDataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val stringBuilder = StringBuilder()

                for(ds in dataSnapshot.getChildren()) {
                    val myNotification: MyNotification? = ds.getValue(MyNotification::class.java)

                    val notif: String = "Expr: " + myNotification?.expression + "\n" +
                            "Res: " + myNotification?.result
                    stringBuilder.append(notif).append("\n\n")
                }

                val result: String = stringBuilder.toString().trim()
                historyTextView.text = result
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
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
        val constraintLayout: ConstraintLayout = findViewById(R.id.read_main)
        constraintLayout.setBackgroundColor(bgColorId)
        buttonBack.setBackgroundColor(buttonBgColorId)
        buttonBack.setTextColor(textColorId)

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