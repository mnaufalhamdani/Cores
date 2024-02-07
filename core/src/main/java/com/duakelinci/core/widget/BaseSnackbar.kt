package com.duakelinci.core.widget

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.duakelinci.core.R
import com.duakelinci.core.data.model.TypeMessage
import com.duakelinci.core.databinding.MessageSnackbarBinding
import com.google.android.material.snackbar.Snackbar

class BaseSnackbar() {
    @SuppressLint("RestrictedApi")
    fun init(
        view: View,
        typeMessage: TypeMessage,
        message: String = "unknown Message",
        duration: Int = Snackbar.LENGTH_SHORT,
        paddingBottom: Int = 0,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ): Snackbar {
        val drawableId = when(typeMessage) {
            TypeMessage.SUCCESS -> R.drawable.img_anim_success
            TypeMessage.INFO    -> R.drawable.img_anim_info
            TypeMessage.WARNING -> R.drawable.img_anim_warning
            TypeMessage.ERROR   -> R.drawable.img_anim_error
            TypeMessage.OTHER   -> R.drawable.img_anim_warning
        }

        val colorId = when(typeMessage) {
            TypeMessage.SUCCESS -> R.color.green_A700
            TypeMessage.INFO    -> R.color.blue_A700
            TypeMessage.WARNING -> R.color.amber_A700
            TypeMessage.ERROR   -> R.color.red_A700
            TypeMessage.OTHER   -> R.color.grey_700
        }

        val snackbar = Snackbar.make(view, "", duration)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, paddingBottom)

        val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM
        snackbar.view.layoutParams = params

        val mBinding = MessageSnackbarBinding.inflate(LayoutInflater.from(view.context))
        mBinding.card.strokeColor = ContextCompat.getColor(view.context, colorId)
        mBinding.message.text = message

        Glide.with(view.context).load(drawableId).into(mBinding.icon)

        mBinding.btnClose.setOnClickListener {
            snackbar.dismiss()
        }

        actionText?.let {
            mBinding.action.apply {
                visibility = View.VISIBLE
                text = it
                setOnClickListener {
                    action?.invoke()
                }
            }
        }

        snackbarLayout.addView(mBinding.root, 0)
        return snackbar
    }
}