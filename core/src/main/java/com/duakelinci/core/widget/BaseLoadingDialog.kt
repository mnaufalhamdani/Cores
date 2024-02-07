package com.duakelinci.core.widget

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.bumptech.glide.Glide
import com.duakelinci.core.R
import com.duakelinci.core.databinding.LoadingDialogBinding

class BaseLoadingDialog {
    fun init(view: View): Dialog {
        val mBinding = LoadingDialogBinding.inflate(LayoutInflater.from(view.context))
        val dialog = Dialog(view.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(mBinding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        Glide.with(view.context).load(R.drawable.img_anim_loading_dk).into(mBinding.imgLoading)

        return dialog
    }
}