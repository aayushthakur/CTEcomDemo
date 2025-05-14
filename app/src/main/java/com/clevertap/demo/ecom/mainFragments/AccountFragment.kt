package com.clevertap.demo.ecom.mainFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clevertap.demo.ecom.CTAnalyticsHelper
import com.clevertap.demo.ecom.Constants
import com.clevertap.demo.ecom.IndustrySelectActivity
import com.clevertap.demo.ecom.ProfileActivity
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.SignUpActivity
import com.clevertap.demo.ecom.UtilityHelper
import com.clevertap.demo.ecom.databinding.FragmentAccountBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        binding.include.toolbarText.text = "Account"

        binding.paymentItem.listItemText.text = getString(R.string.payment_settings)
        binding.addressItem.listItemText.text = getString(R.string.manage_address)
        binding.adminItem.listItemText.text = getString(R.string.admin_panel)
        binding.useCasesItem.listItemText.text = getString(R.string.use_case_list)
        binding.industrySelectItem.listItemText.text = getString(R.string.industry_selector)
        binding.notificationsItem.listItemText.text = getString(R.string.manage_notification_settings)
        binding.logoutItem.listItemText.text = getString(R.string.logout)

        binding.paymentItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.credit_card_icon,null))
        binding.addressItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.home_black_24dp,null))
        binding.adminItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.admin_section_icon,null))
        binding.useCasesItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.use_cases_icon,null))
        binding.industrySelectItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.use_cases_icon,null))
        binding.notificationsItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.notif_icon,null))
        binding.logoutItem.listItemIcon.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.logout_icon,null))

       val prefs =  UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(requireContext())
        if (prefs!=null){
            binding.profileName.text = prefs.getString(Constants.name,"User")
            binding.profileEmail.text = prefs.getString(Constants.email,"Email Address")
        }
        binding.profileEdit.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.logoutItem.logoutItemLayout.setOnClickListener(View.OnClickListener {
            val preferences = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(requireContext())
            val editor = preferences?.edit()
            editor?.clear()
            editor?.apply()

            val intent = Intent(context, SignUpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()

        })

        binding.industrySelectItem.logoutItemLayout.setOnClickListener {
            val intent = Intent(context, IndustrySelectActivity::class.java)
            startActivity(intent)
        }

        CTAnalyticsHelper.INSTANCE.pushEvent("Account Page Viewed")

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") = AccountFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}