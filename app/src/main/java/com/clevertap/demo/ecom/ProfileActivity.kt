package com.clevertap.demo.ecom

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.demo.ecom.databinding.ActivityProfileBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val calendar = Calendar.getInstance()
    private lateinit var date: Date
    private val TAG: String = ProfileActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.include.toolbarText.text = getString(R.string.edit_profile)

        binding.apply {
            profileUpdateButton.setOnClickListener { profileUpdateButtonClicked() }
            dobTextInputLayout.setEndIconOnClickListener { showDatePicker() }
        }

//        val theMap = mapOf("Electronics" to 1, "Mobile Phones" to 2, "Fashion" to 3, "Home & Kitchen" to 4, "Health" to 5, "Gift Cards" to 6, "Groceries" to 7)

        val priorities = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, priorities)
        binding.categoryInputEditText.setAdapter(arrayAdapter)

        val prefs = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
        val name = prefs?.getString(Constants.name, "")
        val email = prefs?.getString(Constants.email, "")
        val phone = prefs?.getString(Constants.phone, "")
        val preference = prefs?.getString(Constants.categoryPreference, "")
        val dob = prefs?.getString(Constants.dob, "")
        if (!TextUtils.isEmpty(dob)){
            date = dateToDateObject(dob)
        }else{
            date = Date()
        }
        binding.nameInputEditText.setText(name)
        binding.emailInputEditText.setText(email)
        binding.phoneInputEditText.setText(phone)
        binding.categoryInputEditText.setText(preference, false)
        binding.dobInputEditText.setText("Selected Date: $dob")

        CTAnalyticsHelper.INSTANCE.pushEvent("Edit Profile Page Viewed")

    }

    private fun profileUpdateButtonClicked() {
        val name = binding.nameInputEditText.text
        val email = binding.emailInputEditText.text
        val phone = binding.phoneInputEditText.text
        val category = binding.categoryInputEditText.text

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) &&
            !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(category)) {
            val data: HashMap<String, Any> = hashMapOf(
                "Phone" to phone.toString(),
                "Name" to name.toString(),
                "Preferred Category" to category.toString(),
                "DOB" to date,
            )
            Log.d(TAG, "profileUpdateButtonClicked() called  $data")
            CTAnalyticsHelper.INSTANCE.pushProfile(data)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Format the selected date into a string
            val formattedDate = dateFormat.format(date.time)
            UtilityHelper.INSTANCE.savePIIDataSharedPreference(
                applicationContext,
                email.toString(),
                name.toString(),
                phone.toString(),
                category.toString(),
                formattedDate
            )
            finish()
        }else{
            Toast.makeText(applicationContext,"Please Fill All Details", Toast.LENGTH_SHORT).show()
        }
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

    private fun dateToDateObject(dateString: String?): Date {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateString?.let { formatter.parse(it) }!!
    }

}