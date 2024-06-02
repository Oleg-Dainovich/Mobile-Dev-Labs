package com.example.calculator

import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat

class AuthenticationActivity : AppCompatActivity() {

    private var isHaveBiometric: Boolean = true
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var activityF: Intent
    private lateinit var activityM: Intent

    private lateinit var passwordET: EditText
    private lateinit var bOne: Button
    private lateinit var bTwo: Button
    private lateinit var bThree: Button
    private lateinit var bFour: Button
    private lateinit var bFive: Button
    private lateinit var bSix: Button
    private lateinit var bSeven: Button
    private lateinit var bEight: Button
    private lateinit var bNine: Button
    private lateinit var bZero: Button
    private lateinit var bClear: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_layout)

        activityM = Intent(this, MainActivity::class.java)
        activityF = Intent(this, ForgottenPinActivity::class.java)

        initializeButtons()
        passwordET = findViewById(R.id.password_ed)
        bOne = findViewById(R.id.b_one)
        bTwo = findViewById(R.id.b_two)
        bThree = findViewById(R.id.b_three)
        bFour = findViewById(R.id.b_four)
        bFive = findViewById(R.id.b_five)
        bSix = findViewById(R.id.b_six)
        bSeven = findViewById(R.id.b_seven)
        bEight = findViewById(R.id.b_eight)
        bNine = findViewById(R.id.b_nine)
        bZero = findViewById(R.id.b_zero)
        bClear = findViewById(R.id.b_clear)

        checkBiometricInDevice() // Тут проверяем есть ли вход по биометрии у пользователя

        if (isHaveBiometric) { // Тут проверка
            initializeButtonForBiometric() // тут инициализируем кнопку входа по биометрии
        }

        bOne.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "1")
        }

        bTwo.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "2")
        }

        bThree.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "3")
        }

        bFour.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "4")
        }

        bFive.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "5")
        }

        bSix.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "6")
        }

        bSeven.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "7")
        }

        bEight.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "8")
        }

        bNine.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "9")
        }

        bZero.setOnClickListener {
            val value: String = passwordET.text.toString()
            passwordET.setText(value + "0")
        }

        bClear.setOnClickListener {
            val value: String = passwordET.text.toString()
            if (value.isNotEmpty()) {
                val newValue = value.substring(0, value.length - 1)
                passwordET.setText(newValue)
                passwordET.setSelection(newValue.length)
            }
        }
    }

    private fun initializeButtons() {
        val logInButton = findViewById<Button>(R.id.LogIn)
        val bForgetPin = findViewById<Button>(R.id.pin_forgot)


        logInButton.setOnClickListener {
            val enteredPassword = passwordET.text.toString()
            if (checkPassword(enteredPassword)) {
                startActivity(activityM)
                finish()
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }
        }

        bForgetPin.setOnClickListener {
            startActivity(activityF)

        }
    }

    private fun checkPassword(password: String): Boolean {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("pin", "")
        return savedPin == password
    }


    private fun checkBiometricInDevice() {
        val biometricManager = BiometricManager.from(this)
        val buttonOpenBiometric = findViewById<Button>(R.id.biometrics)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                buttonOpenBiometric.visibility = View.VISIBLE
                isHaveBiometric = true
            }

            else -> {
                buttonOpenBiometric.visibility = View.GONE
                isHaveBiometric = false
            }
        }
    }

    private fun initializeButtonForBiometric() {
        val buttonBiometric = findViewById<Button>(R.id.biometrics)

        biometricPrompt = BiometricPrompt(this@AuthenticationActivity, ContextCompat.getMainExecutor(this), object:androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val cryptoObject = result.cryptoObject
                startActivity(activityM)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(this@AuthenticationActivity, "Authentication error", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@AuthenticationActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })

        buttonBiometric.setOnClickListener {
            biometricPrompt.authenticate(createBiometricPromptInfo())
        }
    }

    private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authorization")
            .setNegativeButtonText("Cancel")
            .build()
    }
}
