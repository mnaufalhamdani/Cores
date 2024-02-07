package com.duakelinci.core

interface BaseImplDialog {
    fun hideDialog()

    fun showLoadingDialog()

    fun showAlertDialog(
        title: String?,
        message: String,
        isCancelOutside: Boolean? = false,
        actionText: String? = null,
        action: (() -> Unit)? = null
    )

    fun showFullscreenAlertDialog(
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    )
}