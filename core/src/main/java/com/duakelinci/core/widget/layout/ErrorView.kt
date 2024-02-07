package com.duakelinci.core.widget.layout

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.duakelinci.core.R
import com.duakelinci.core.util.getString
import com.duakelinci.core.util.visible

class ErrorView : RelativeLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init()
    }

    private fun init() {
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.error_view, this)
    }

    fun setView(
        message: String?,
        case: Int? = 4,
        @DrawableRes pIcon:Int?=null,
        onReload: (() -> Unit)? = null
    ) {
        val icon: Int
        findViewById<ConstraintLayout>(R.id.lyt_disconnect).visible()
        findViewById<TextView>(R.id.txt_error).text = message
        findViewById<ImageView>(R.id.img_error).visible()
        findViewById<LinearLayout>(R.id.lyt_offline).visible()
        when (case) {
            0 -> {
                if (message.isNullOrBlank()) findViewById<TextView>(R.id.txt_error).text =
                    getString(R.string.base_error_permission)
                icon = R.drawable.img_anim_no_permission
            }
            1 -> {
                if (message.isNullOrBlank()) findViewById<TextView>(R.id.txt_error).text =
                    getString(R.string.base_error_no_gps)
                icon = R.drawable.img_anim_no_gps
            }
            2 -> {
                if (message.isNullOrBlank()) findViewById<TextView>(R.id.txt_error).text =
                    getString(R.string.base_error_connection)
                icon = R.drawable.img_anim_no_connection
            }
            4 -> {
                if (message.isNullOrBlank()) findViewById<TextView>(R.id.txt_error).text =
                    getString(R.string.base_error_no_data)
                icon = R.drawable.img_anim_no_data
            }
            else -> {
                if (message.isNullOrBlank()) findViewById<TextView>(R.id.txt_error).text =
                    getString(R.string.base_error_unknown)
                icon = R.drawable.img_anim_bug
            }
        }
        Glide.with(context)
            .load(icon)
            .into(findViewById(R.id.img_error))
        pIcon?.let { findViewById<ImageView>(R.id.img_error).setImageResource(it) }
        findViewById<LinearLayout>(R.id.lyt_offline).setOnClickListener {
            onReload?.invoke()
        }
    }
}