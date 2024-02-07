package com.duakelinci.core.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.duakelinci.core.R
import com.duakelinci.core.databinding.ConfirmAlertDialogBinding
import com.duakelinci.core.util.visible

class BaseAlertDialog {
    @SuppressLint("ResourceType")
    fun init(
        view: View,
        message: String,
        title: String? = null,
        isCancelOutside: Boolean = false,
        actionText: String? = null, action: (() -> Unit)? = null
    ): Dialog {
        val mBinding = ConfirmAlertDialogBinding.inflate(LayoutInflater.from(view.context))
        val dialog = Dialog(view.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(mBinding.root)
        dialog.setCancelable(isCancelOutside)
        dialog.setCanceledOnTouchOutside(isCancelOutside)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        mBinding.txtMessage.text = message
        title?.let {
            mBinding.txtTitle.visible()
            mBinding.txtTitle.text = it
        }

        actionText?.let {
            mBinding.btnPositive.visible()
            mBinding.btnPositive.text = actionText
            mBinding.btnPositive.setOnClickListener {
                action?.invoke()
            }
        }

        mBinding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}