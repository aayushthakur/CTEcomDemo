package com.clevertap.demo.ecom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.clevertap.demo.ecom.databinding.ActivityIndustrySelectBinding


class IndustrySelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIndustrySelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_industry_select)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.include.toolbarText.text = getString(R.string.industry_selector)

        binding.apply {
            industryEcommerceSelection.setOnClickListener { ecommerceSelected() }
            industryFintechSelection.setOnClickListener() { fintechSelected() }
        }
    }

    private fun ecommerceSelected() {
        UtilityHelper.INSTANCE.saveIndustrySelectionSharedPreference(
            applicationContext,
            Constants.ECOMMERCE
        )
        closeActivity()
    }

    private fun fintechSelected() {
        UtilityHelper.INSTANCE.saveIndustrySelectionSharedPreference(
            applicationContext,
            Constants.FINTECH
        )
        closeActivity()
    }

    private fun closeActivity(){
        val intent = Intent(
            this@IndustrySelectActivity,
            MainActivity::class.java
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}