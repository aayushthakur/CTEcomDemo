package com.clevertap.demo.ecom

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.demo.ecom.api.APIRecordPOJO
import com.clevertap.demo.ecom.api.APIResultPOJO
import com.clevertap.demo.ecom.api.ApiInterface
import com.clevertap.demo.ecom.api.RetrofitInstance
import com.clevertap.demo.ecom.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiInterface: ApiInterface
    private val TAG: String = LoginActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        val prefEmail = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
            ?.getString(Constants.email, "")

        if (!TextUtils.isEmpty(prefEmail)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.include.toolbarText.text = getString(R.string.login)
        binding.apply {
            loginButton.setOnClickListener { getProfileDataViaEmail() }
            newUserTextview.setOnClickListener { openSignUpActivity() }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CTAnalyticsHelper.INSTANCE.pushEvent("Login Page Viewed")
        apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)

    }

    private fun getProfileDataViaEmail() {
        val email = binding.emailInputEditText.text
        if (!TextUtils.isEmpty(email) && UtilityHelper.INSTANCE.isValidEmail(email.toString())) {
            showHideProgressView(true)

            val call = apiInterface.getProfileViaEmail(
                email.toString(),
                UtilityHelper.INSTANCE.getHeaderMap()
            )
            call.enqueue(object : Callback<APIResultPOJO> {
                override fun onResponse(
                    call: Call<APIResultPOJO>,
                    response: Response<APIResultPOJO>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val recordPojo = response.body()!!.record
                        Log.d(TAG, "onResponse() called with: call = $call, response = $response")
                        if (recordPojo == null) {
                            runOnUiThread(Runnable {
                                //do something
                                showHideProgressView(false)
                                Toast.makeText(
                                    applicationContext,
                                    "User Not Found, Please Sign Up",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })

                        } else {
                            loginButtonClicked(recordPojo)
                        }
                    }else if(!response.isSuccessful){
                        Log.d(TAG, "onResponse() called with: call = $call, response = ${response.body()}")
                        Log.d(TAG, "onResponse() called with: call = $call, response = ${response.errorBody()}")
                        runOnUiThread(Runnable {
                            //do something
                            showHideProgressView(false)

                        })
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<APIResultPOJO>, t: Throwable) {
                    t.printStackTrace()
                    runOnUiThread(Runnable {
                        //do something
                        showHideProgressView(false)
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT)
                            .show()
                    })
                }
            })
        } else {
            Toast.makeText(
                applicationContext,
                "Please Enter Valid Email ID to proceed",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun loginButtonClicked(recordPojo: APIRecordPOJO) {
        val email = binding.emailInputEditText.text
        if (!TextUtils.isEmpty(email)) {
            Timer().schedule(1500) {
                runOnUiThread(Runnable {
                    //do something
                    showHideProgressView(false)

                })

                saveDataToPrefs(recordPojo)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {
            runOnUiThread(Runnable {
                //do something
                showHideProgressView(false)

            })
            Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveDataToPrefs(recordPojo: APIRecordPOJO) {
        val name = recordPojo.name
        val email = recordPojo.email
        val profileData = recordPojo.profileData
        var dob = ""
        var preferredCategory = ""
        var preferredTheme = ""
        if (profileData != null) {
            dob = profileData.dob!!
            preferredCategory = profileData.preferredcategory!!
            preferredTheme = profileData.preferredtheme!!
        }
        if (!TextUtils.isEmpty(dob)) {
            // Extract the value after the underscore
            val epochTimeString = dob.substringAfter("_")
            // Convert the extracted string to a Long (epoch time in seconds)
            val epochTime = epochTimeString.toLong()
            dob = UtilityHelper.INSTANCE.getDateTime(epochTime)
        }

        val platformInfo = recordPojo.platformInfo
        var phone = ""
        if (platformInfo.isNotEmpty()) {
            for (data in platformInfo) {
                if (!TextUtils.isEmpty(data.phone)) {
                    phone = data.phone!!
                    break
                }
            }
        }
        Log.d(
            TAG,
            "saveDataToPrefs() called with: name = $name email = $email preferredCategory = $preferredCategory dob =  $dob  phone = $phone"
        )
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name)
            && !TextUtils.isEmpty(phone)
            && !TextUtils.isEmpty(preferredCategory) && !TextUtils.isEmpty(preferredTheme) && !TextUtils.isEmpty(dob)
        ) {

            UtilityHelper.INSTANCE.savePIIDataSharedPreference(
                applicationContext,
                email!!, name, "+$phone", preferredCategory, preferredTheme, dob
            )

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Format the selected date into a string
            val date = dateFormat.parse(dob)

            val data: HashMap<String, Any> = hashMapOf(
                "Email" to email,
                "Phone" to "+$phone",
                "Name" to name.toString(),
                "Preferred Category" to preferredCategory,
                "Preferred Theme" to preferredTheme,
                ("DOB" to date) as Pair<String, Any>,
                "MSG-sms" to true,
                "MSG-email" to true,
                "MSG-whatsapp" to true
            )
            Log.d(TAG, "signUpButtonClicked() called  $data")
            CTAnalyticsHelper.INSTANCE.onUserLogin(data)
        }
    }

    private fun openSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showHideProgressView(show : Boolean){
        if (show){
            binding.llProgressBar.progressLayout.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            binding.llProgressBar.progressLayout.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }


    }

}