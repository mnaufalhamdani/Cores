@file:Suppress("DEPRECATION")

package com.duakelinci.core.widget

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.duakelinci.core.R
import com.duakelinci.core.data.model.TypeMessage
import com.duakelinci.core.databinding.MessageToastBinding
import com.duakelinci.core.util.CoreUtil

class BaseToast {
    fun init(
        view: View,
        typeMessage: TypeMessage,
        message: String = "unknown Message",
        duration: Int = Toast.LENGTH_SHORT,
        paddingBottom: Int? = 0,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ): Toast {
        val colorId = when(typeMessage) {
            TypeMessage.SUCCESS -> R.color.green_A700
            TypeMessage.INFO    -> R.color.blue_A700
            TypeMessage.WARNING -> R.color.amber_A700
            TypeMessage.ERROR   -> R.color.red_A700
            TypeMessage.OTHER   -> R.color.grey_700
        }

        val toast = Toast(view.context)
        toast.duration = duration

        val mBinding = MessageToastBinding.inflate(LayoutInflater.from(view.context))
        mBinding.message.text = message
        mBinding.card.setCardBackgroundColor(ContextCompat.getColor(view.context, colorId))
        actionText?.let {
            mBinding.action.apply {
                visibility = View.VISIBLE
                text = it
                setOnClickListener {
                    action?.invoke()
                }
            }
        }

        toast.view = mBinding.root
        toast.setGravity(Gravity.BOTTOM, 0, CoreUtil.dpToPx(view.context, paddingBottom ?: 0))
        return toast
    }
}