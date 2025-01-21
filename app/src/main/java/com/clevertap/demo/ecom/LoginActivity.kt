package com.clevertap.demo.ecom

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.demo.ecom.databinding.ActivityLoginBinding
import java.util.Timer
import kotlin.concurrent.schedule


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefEmail = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
            ?.getString(Constants.email,"");

        if (!TextUtils.isEmpty(prefEmail)){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.include.toolbarText.text = getString(R.string.login)
        binding.apply {
            loginButton.setOnClickListener { loginButtonClicked() }
            newUserTextview.setOnClickListener{openSignUpActivity()}
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CTAnalyticsHelper.INSTANCE.pushEvent("Login Page Viewed");

    }

    private fun loginButtonClicked() {
        val email = binding.emailInputEditText.text
        if (!TextUtils.isEmpty(email)){
//            val data: HashMap<String, Any> = hashMapOf("Email" to email, "Identity" to password)
            val data: HashMap<String, Any> = hashMapOf("Email" to email.toString())
            CTAnalyticsHelper.INSTANCE.onUserLogin(data)
//            Toast.makeText(applicationContext,"Login Successful", Toast.LENGTH_SHORT).show()
            UtilityHelper.INSTANCE.savePIIDataSharedPreference(applicationContext,email.toString(),null,null,null,null)

            val mProgressDialog = ProgressDialog(this)
            mProgressDialog.setTitle("Please Wait")
            mProgressDialog.setMessage("Logging You In...")
            mProgressDialog.show()

            Timer().schedule(1500){
                runOnUiThread(Runnable {
                    //do something
                    mProgressDialog.hide();
                })

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }else{
            Toast.makeText(applicationContext,"Login Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openSignUpActivity(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}