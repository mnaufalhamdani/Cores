package com.duakelinci.core.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.bumptech.glide.Glide
import com.duakelinci.core.R
import com.duakelinci.core.databinding.FullscreenAlertDialogBinding

class BaseFullscreenAlertDialog {
    @SuppressLint("ResourceType")
    fun init(
        view: View,
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ): Dialog {
        val mBinding = FullscreenAlertDialogBinding.inflate(LayoutInflater.from(view.context))
        val dialog = Dialog(view.context, R.style.ThemeMaterial)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(mBinding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        Glide.with(view.context).load(R.drawable.img_anim_warning).into(mBinding.imgWarning)
        mBinding.txtMessage.text = message
        actionText?.let {
            mBinding.btnWarning.text = it
            mBinding.btnWarning.setOnClickListener {
                action?.invoke()
            }
        }

        return dialog
    }
}