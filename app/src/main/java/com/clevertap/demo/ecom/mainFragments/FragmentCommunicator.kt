package com.clevertap.demo.ecom.mainFragments

import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit


interface FragmentCommunicator {

    fun loadData(units: ArrayList<CleverTapDisplayUnit>)

}