package com.duakelinci.core

import android.widget.Toast
import com.duakelinci.core.data.model.TypeMessage
import com.google.android.material.snackbar.Snackbar

interface BaseImplMessage {
    fun dismissSnackbarMessage()

    fun showSnackbarMessage(
        typeMessage: TypeMessage,
        message: String,
        duration: Int? = Snackbar.LENGTH_SHORT,
        paddingBottom: Int? = 0,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ): Snackbar?

    fun showToastMessage(
        typeMessage: TypeMessage,
        message: String,
        duration: Int? = Toast.LENGTH_SHORT,
        paddingBottom: Int? = 0,
        actionText: String? = null,
        action: (() -> Unit)? = null
    )
}