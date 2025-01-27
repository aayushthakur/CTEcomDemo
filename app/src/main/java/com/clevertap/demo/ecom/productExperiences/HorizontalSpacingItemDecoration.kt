package com.clevertap.demo.ecom.productExperiences

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingItemDecoration(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.adapter !=null && parent.getChildLayoutPosition(view)!= parent.adapter!!.itemCount -1){
//            outRect.left = horizontalSpacing
            outRect.right = horizontalSpacing
        }
    }
}