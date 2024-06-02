package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import net.objecthunter.exp4j.ExpressionBuilder

import android.media.MediaPlayer
import android.os.Vibrator;
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var tvFormula: TextView;
    private lateinit var tvResult: TextView;

    private lateinit var historyButton: MaterialButton
    private lateinit var settingsButton: MaterialButton

    private lateinit var backspaceButton: MaterialButton
    private lateinit var clearButton: MaterialButton
    private lateinit var plusButton: MaterialButton
    private lateinit var divideButton: MaterialButton
    private lateinit var multiplyButton: MaterialButton
    private lateinit var minusButton: MaterialButton
    private lateinit var equalButton: MaterialButton
    private lateinit var sqrtButton: MaterialButton
    private lateinit var powerButton: MaterialButton
    private lateinit var sinButton: MaterialButton
    private lateinit var cosButton: MaterialButton
    private lateinit var lnButton: MaterialButton
    private lateinit var parenthesesButton: MaterialButton
    private lateinit var percentageButton: MaterialButton
    private lateinit var dotButton: MaterialButton

    private lateinit var oneButton: MaterialButton
    private lateinit var twoButton: MaterialButton
    private lateinit var threeButton: MaterialButton
    private lateinit var fourButton: MaterialButton
    private lateinit var fiveButton: MaterialButton
    private lateinit var sixButton: MaterialButton
    private lateinit var sevenButton: MaterialButton
    private lateinit var eightButton: MaterialButton
    private lateinit var nineButton: MaterialButton
    private lateinit var zeroButton: MaterialButton

    private var currentExpression = StringBuilder();
    private var currentResult = 0.0;
    private var lastOperator = "";
    private val MAX_RESULT_LENGTH = 12;
    private val MAX_EXPRESSION_LENGTH = 20;
    private val OPERATORS = "+-*/^";
    private val PARENTHESES = "()";
    private val MAX_INPUT_VALUE: Double = 1e32;

    private var mediaPlayer: MediaPlayer? = null;
    private lateinit var vibrator: Vibrator;
    private val captureRequestCode = 1;

    private val NOTIFICATION_KEY: String = "MyNotification";
    private val THEME_KEY: String = "MyTheme";
    private lateinit var myDataBase: DatabaseReference;
    private lateinit var myThemeDataBase: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myDataBase = FirebaseDatabase.getInstance().getReference(NOTIFICATION_KEY)
        myThemeDataBase = FirebaseDatabase.getInstance().getReference(THEME_KEY)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.e("TAG", "Token -> $token")
        })

        setContentView(R.layout.activity_main)
        tvFormula = findViewById(R.id.tvFormula)
        tvResult = findViewById(R.id.tvResult)

        historyButton = findViewById(R.id.history)
        settingsButton = findViewById(R.id.settings)

        backspaceButton = findViewById(R.id.backspace)
        clearButton = findViewById(R.id.clear)
        plusButton = findViewById(R.id.plus)
        divideButton = findViewById(R.id.devide)
        multiplyButton = findViewById(R.id.multiply)
        minusButton = findViewById(R.id.mines)
        equalButton = findViewById(R.id.equal)
        sqrtButton = findViewById(R.id.sqrt)
        powerButton = findViewById(R.id.power)
        sinButton = findViewById(R.id.sin)
        cosButton = findViewById(R.id.cos)
        lnButton = findViewById(R.id.ln)
        parenthesesButton = findViewById(R.id.parentheses)
        percentageButton = findViewById(R.id.percentage)
        dotButton = findViewById(R.id.dot)

        oneButton = findViewById(R.id.one)
        twoButton = findViewById(R.id.two)
        threeButton = findViewById(R.id.three)
        fourButton = findViewById(R.id.four)
        fiveButton = findViewById(R.id.five)
        sixButton = findViewById(R.id.six)
        sevenButton = findViewById(R.id.seven)
        eightButton = findViewById(R.id.eight)
        nineButton = findViewById(R.id.nine)
        zeroButton = findViewById(R.id.zero)

        historyButton.setOnClickListener(buttonClickListener)
        settingsButton.setOnClickListener(buttonClickListener)

        backspaceButton.setOnClickListener(buttonClickListener)
        clearButton.setOnClickListener(buttonClickListener)
        plusButton.setOnClickListener(buttonClickListener)
        divideButton.setOnClickListener(buttonClickListener)
        sqrtButton.setOnClickListener(buttonClickListener)
        multiplyButton.setOnClickListener(buttonClickListener)
        minusButton.setOnClickListener(buttonClickListener)
        equalButton.setOnClickListener(buttonClickListener)
        powerButton.setOnClickListener(buttonClickListener)
        sinButton.setOnClickListener(buttonClickListener)
        cosButton.setOnClickListener(buttonClickListener)
        lnButton.setOnClickListener(buttonClickListener)
        parenthesesButton.setOnClickListener(buttonClickListener)
        percentageButton.setOnClickListener(buttonClickListener)
        dotButton.setOnClickListener(buttonClickListener)

        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.hell_nah)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        val digitButtons = arrayOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton)
        digitButtons.forEach { button ->
            button.setOnClickListener(buttonClickListener)
        }

        chooseTheme("0")
    }
    val buttonClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.history -> {
                showHistory()
            }
            R.id.settings -> {
                setSettings()
            }
            R.id.backspace -> {
                removeLast()
            }
            R.id.clear -> {
                clearExpression()
            }
            R.id.dot ->{
                appendDot()
                updateUI()
            }
            R.id.plus, R.id.mines, R.id.multiply, R.id.devide -> {
                val lastChar = if (currentExpression.isNotEmpty()) currentExpression.last() else ' '

                val isLastCharOperator = lastChar in listOf('+', '-', '*', '/')

                if (!isLastCharOperator) {
                    handleOperator((view as MaterialButton).text.toString())
                }
            }
            R.id.equal -> {
                calculateResult()
            }
            R.id.sqrt -> {
                currentExpression.append("sqrt(")
                updateUI()
            }
            R.id.power -> {
                currentExpression.append("^2")
                updateUI()
            }
            R.id.sin -> {
                currentExpression.append("sin(")
                updateUI()
            }
            R.id.cos -> {
                currentExpression.append("cos(")
                updateUI()
            }
            R.id.ln -> {
                currentExpression.append("ln(")
                updateUI()
            }
            R.id.parentheses -> {
                val openParenthesesNeeded = currentExpression.count { it == '(' } > currentExpression.count { it == ')' }

                val parenthesesToAdd = if (!openParenthesesNeeded) "(" else ")"

                val lastOperatorIndex = currentExpression.lastIndexOfAny(OPERATORS.toCharArray())
                val lastParenthesesIndex = currentExpression.lastIndexOfAny(PARENTHESES.toCharArray())
                val lastSymbolIndex = maxOf(lastOperatorIndex, lastParenthesesIndex)
                val isLastSymbolOperator = lastSymbolIndex >= 0 && currentExpression[lastSymbolIndex] in OPERATORS

                if (currentExpression.isEmpty() || currentExpression.endsWith('(') || isLastSymbolOperator) {
                    currentExpression.append(parenthesesToAdd)
                } else {
                    currentExpression.append(parenthesesToAdd)
                }
                updateUI()
            }
            R.id.percentage -> {
                onPercentageButtonClick()
            }
            else -> {
                val digit = (view as MaterialButton).text.toString()
                appendDigit(digit)
            }
        }
    }

    private fun appendDot() {
        val lastChar = currentExpression.lastOrNull()

        if (currentExpression.isEmpty() || lastChar!! in OPERATORS.toCharArray()) {
            currentExpression.append("0.")
        } else if (lastChar != '.') {
            currentExpression.append(".")
        }
        updateUI()
    }

    private fun handleOperator(operator: String) {
        val lastChar = if (currentExpression.isNotEmpty()) currentExpression.last() else ' '

        val isLastCharOperator = lastChar in listOf('+', '-', '*', '/')

        if (!isLastCharOperator) {
            currentExpression.append(" $operator ")
            lastOperator = operator
            updateUI()
        } else {
            currentExpression.setLength(currentExpression.length - 1)
            currentExpression.append(" $operator ")
            lastOperator = operator
            updateUI()
        }
    }

    private fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    private fun removeLast() {
        val currentExpr = tvFormula.text

        if (!currentExpr!!.isEmpty()) {
            tvFormula.setText(currentExpr.substring(0, currentExpr.length - 1))
            currentExpression = StringBuilder(tvFormula.text)
        }
    }

    private fun clearExpression() {
        currentExpression.clear()
        currentResult = 0.0
        tvFormula.text = ""
        tvResult.text = "0"
    }

    private fun calculateResult() {
        val expression = currentExpression.toString()

        val expressionWithPercentConverted = expression.replace("%", "* 0.01")

        try {
            val exp4jExpression = ExpressionBuilder(expressionWithPercentConverted).build()
            val result = exp4jExpression.evaluate()
            if(result>=MAX_INPUT_VALUE){
                clearExpression()
                saveNotification(expression, "Number is too large");
                updateUI()
                apiUsage()
                showToast("Number is too large")
                return
            }
            val formattedResult = if (result.toString().length > MAX_RESULT_LENGTH) {
                result.toString().substring(0, MAX_RESULT_LENGTH)
            } else {
                result.toString()
            }

            currentExpression.clear()
            currentExpression.append(formattedResult)
            currentResult = formattedResult.toDouble()

            if (currentResult == 10.0) {
                cameraUsage()
            }

            saveNotification(expression, currentResult.toString());

            updateUI()
        } catch (e: Exception) {
            apiUsage()
            saveNotification(expression, "Calculation error");
            showToast("Calculation error")
        }
    }

    private fun appendDigit(digit: String) {
        if (currentExpression.length + digit.length <= MAX_EXPRESSION_LENGTH) {
            currentExpression.append(digit)
            updateUI()
        } else {
            apiUsage()
            showToast("Maximum number length exceeded")
        }
        Log.d("AppendDigit", "Length of expression: ${currentExpression.length}")
    }

    private fun updateUI() {
        tvFormula.text = currentExpression.toString()
        tvResult.text = currentResult.toString()
    }

    private fun onPercentageButtonClick() {
        currentExpression.append("%")
        updateUI()
    }

    private fun apiUsage() {
        mediaPlayer?.start()
        vibrator.vibrate(500)
    }

    private fun cameraUsage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                captureRequestCode
            )
        }
    }

    private fun openCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (captureIntent.resolveActivity(packageManager) != null) {
            captureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1) // фронталка
            startActivityForResult(captureIntent, captureRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == captureRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == captureRequestCode && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            try {
                val uri = saveImage(imageBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun saveImage(bitmap: Bitmap): String {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Фото",
            "Описание фото"
        )
        return savedImageURL.toString()
    }

    private fun saveNotification(expr: String, res: String) {
        var id: String? = myDataBase.getKey();
        var expression: String = expr;
        var result: String = res;

        var newNotification = MyNotification(id, expression, result);
        myDataBase.push().setValue(newNotification);
        showToast("Result saved")
    }

    private fun showHistory() {
        var intent = Intent(this, ReadActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setSettings() {
        var intent = Intent(this, ThemeActivity::class.java)
        startActivity(intent)
        finish()
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
        val constraintLayout: ConstraintLayout = findViewById(R.id.layout_main)
        constraintLayout.setBackgroundColor(bgColorId)

        tvFormula.setTextColor(textColorId)
        tvResult.setTextColor(textColorId)

        historyButton.setBackgroundColor(buttonBgColorId)
        settingsButton.setBackgroundColor(buttonBgColorId)

        if(_button_bg_color == "#003566") {
            backspaceButton.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            clearButton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_yellow))
        }
        else {
            backspaceButton.setBackgroundColor(buttonBgColorId)
            clearButton.setBackgroundColor(buttonBgColorId)
        }
        plusButton.setBackgroundColor(buttonBgColorId)
        divideButton.setBackgroundColor(buttonBgColorId)
        multiplyButton.setBackgroundColor(buttonBgColorId)
        minusButton.setBackgroundColor(buttonBgColorId)
        equalButton.setBackgroundColor(buttonBgColorId)
        sqrtButton.setBackgroundColor(buttonBgColorId)
        powerButton.setBackgroundColor(buttonBgColorId)
        sinButton.setBackgroundColor(buttonBgColorId)
        cosButton.setBackgroundColor(buttonBgColorId)
        lnButton.setBackgroundColor(buttonBgColorId)
        parenthesesButton.setBackgroundColor(buttonBgColorId)
        percentageButton.setBackgroundColor(buttonBgColorId)

        dotButton.setBackgroundColor(numBgColorId)
        oneButton.setBackgroundColor(numBgColorId)
        twoButton.setBackgroundColor(numBgColorId)
        threeButton.setBackgroundColor(numBgColorId)
        fourButton.setBackgroundColor(numBgColorId)
        fiveButton.setBackgroundColor(numBgColorId)
        sixButton.setBackgroundColor(numBgColorId)
        sevenButton.setBackgroundColor(numBgColorId)
        eightButton.setBackgroundColor(numBgColorId)
        nineButton.setBackgroundColor(numBgColorId)
        zeroButton.setBackgroundColor(numBgColorId)

        historyButton.setTextColor(textColorId)
        settingsButton.setTextColor(textColorId)

        backspaceButton.setTextColor(textColorId)
        clearButton.setTextColor(textColorId)
        plusButton.setTextColor(textColorId)
        divideButton.setTextColor(textColorId)
        multiplyButton.setTextColor(textColorId)
        minusButton.setTextColor(textColorId)
        equalButton.setTextColor(textColorId)
        sqrtButton.setTextColor(textColorId)
        powerButton.setTextColor(textColorId)
        sinButton.setTextColor(textColorId)
        cosButton.setTextColor(textColorId)
        lnButton.setTextColor(textColorId)
        parenthesesButton.setTextColor(textColorId)
        percentageButton.setTextColor(textColorId)

        dotButton.setTextColor(textColorId)
        oneButton.setTextColor(textColorId)
        twoButton.setTextColor(textColorId)
        threeButton.setTextColor(textColorId)
        fourButton.setTextColor(textColorId)
        fiveButton.setTextColor(textColorId)
        sixButton.setTextColor(textColorId)
        sevenButton.setTextColor(textColorId)
        eightButton.setTextColor(textColorId)
        nineButton.setTextColor(textColorId)
        zeroButton.setTextColor(textColorId)

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
