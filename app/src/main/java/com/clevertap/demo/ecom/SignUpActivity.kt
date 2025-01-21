package com.clevertap.demo.ecom

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.demo.ecom.databinding.ActivitySignUpBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule

class SignUpActivity : AppCompatActivity() {


    private val calendar = Calendar.getInstance()
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var date: Date
    private val TAG: String = SignUpActivity::class.java.simpleName

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.include.toolbarText.text = getString(R.string.sign_up)
        binding.apply {
            signupButton.setOnClickListener { signUpButtonClicked() }
            dobTextInputLayout.setEndIconOnClickListener { showDatePicker() }
            existingUserTextview.setOnClickListener { openLoginActivity() }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CTAnalyticsHelper.INSTANCE.pushEvent("Signup Page Viewed")

        val priorities = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, priorities)
        binding.categoryInputEditText.setAdapter(arrayAdapter)

    }

    private fun signUpButtonClicked() {
        val name = binding.nameInputEditText.text
        val email = binding.emailInputEditText.text
        val phone = binding.phoneInputEditText.text
        val category = binding.categoryInputEditText.text

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(
                category) && date.time != 0L) {
            val data: HashMap<String, Any> = hashMapOf(
                "Email" to email.toString(),
                "Phone" to phone.toString(),
                "Name" to name.toString(),
                "Preferred Category" to category.toString(),
                "DOB" to date,
                "MSG-sms" to true,
                "MSG-email" to true,
                "MSG-whatsapp" to true
            )
            Log.d(TAG, "signUpButtonClicked() called  $data")
            CTAnalyticsHelper.INSTANCE.onUserLogin(data)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Format the selected date into a string
            val formattedDate = dateFormat.format(date.time)
            UtilityHelper.INSTANCE.savePIIDataSharedPreference(applicationContext,email.toString(),name.toString(),phone.toString(),category.toString(),formattedDate)

            val mProgressDialog = ProgressDialog(this)
            mProgressDialog.setTitle("Please Wait")
            mProgressDialog.setMessage("Signing You Up...")
            mProgressDialog.show()

            Timer().schedule(1500) {
                runOnUiThread {
                    mProgressDialog.hide()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            Toast.makeText(applicationContext, "Signup Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showDatePicker() {
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                binding.dobInputEditText.setText("Selected Date: $formattedDate")
                try {
                    date = dateFormat.parse(formattedDate)!!
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }
}