package com.clevertap.demo.ecom

import com.clevertap.android.sdk.inapp.customtemplates.CustomTemplateContext
import com.clevertap.android.sdk.inapp.customtemplates.TemplatePresenter

class MyTemplatePresenter : TemplatePresenter {
    override fun onClose(context: CustomTemplateContext.TemplateContext) {
        context.setDismissed()
    }

    override fun onPresent(context: CustomTemplateContext.TemplateContext) {
        context.setPresented()
    }

}