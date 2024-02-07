package com.duakelinci.core

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import com.duakelinci.core.data.model.TypeMessage
import com.duakelinci.core.widget.BaseAlertDialog
import com.duakelinci.core.widget.BaseFullscreenAlertDialog
import com.duakelinci.core.widget.BaseLoadingDialog
import com.duakelinci.core.widget.BaseSnackbar
import com.duakelinci.core.widget.BaseToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import timber.log.Timber

abstract class BaseBottomSheetFragmentViewBinding<T : ViewBinding> : BottomSheetDialogFragment(),
    BaseImplDialog, BaseImplMessage{

    private var binding: T? = null
    protected val mBinding get() = binding!!

    private var mDialog: Dialog? = null
    private var mSnackbar: Snackbar? = null
    protected val bundle = Bundle()

    abstract fun getTagName(): String
    abstract fun setBinding(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun onCreateUI(view: View, savedInstanceState: Bundle?)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog){
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("${getTagName()} onCreateView() called")
        this.binding = this.setBinding(inflater, container)
//        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("${getTagName()} onViewCreated() called")
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        onCreateUI(view, savedInstanceState)
    }

    override fun dismiss() {
        super.dismiss()
        Timber.i("${getTagName()} dismiss() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("${getTagName()} onDestroy() called")
        this.binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if (activity?.isKeyboardVisible() == true) {
//            mBinding.root.hideSoftKeyboard()
//        }
        Timber.i("${getTagName()} onDestroyView() called")
    }

    // TO PREVENT DOUBLE CLICK
    private var mLastClickTime: Long = 0
    protected fun singleClick():Boolean {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return false
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return true
    }

    override fun hideDialog() {
        if (mDialog != null && mDialog?.isShowing!!) {
            mDialog?.dismiss()
            mDialog = null
        }
    }

    override fun showLoadingDialog() {
        hideDialog()
        mDialog = BaseLoadingDialog().init(mBinding.root)
        mDialog?.show()
    }

    override fun showAlertDialog(
        title: String?,
        message: String,
        isCancelOutside: Boolean?,
        actionText: String?,
        action: (() -> Unit)?
    ) {
        hideDialog()
        mDialog = BaseAlertDialog().init(
            mBinding.root,
            message,
            title,
            isCancelOutside ?: false,
            actionText,
            action
        )
        mDialog?.show()
    }

    override fun showFullscreenAlertDialog(
        message: String,
        actionText: String?,
        action: (() -> Unit)?
    ) {
        hideDialog()
        mDialog = BaseFullscreenAlertDialog().init(
            mBinding.root,
            message,
            actionText,
            action
        )
        mDialog?.show()
    }

    override fun dismissSnackbarMessage() {
        mSnackbar?.let {
            if (it.isShown) it.dismiss()
        }
    }

    override fun showSnackbarMessage(
        typeMessage: TypeMessage,
        message: String,
        duration: Int?,
        paddingBottom: Int?,
        actionText: String?,
        action: (() -> Unit)?
    ): Snackbar? {
        dismissSnackbarMessage()
        mSnackbar = BaseSnackbar().init(
            mBinding.root,
            typeMessage,
            message,
            duration ?: Snackbar.LENGTH_SHORT,
            paddingBottom ?: 0,
            actionText,
            action
        )
        mSnackbar?.show()

        return  mSnackbar
    }

    override fun showToastMessage(
        typeMessage: TypeMessage,
        message: String,
        duration: Int?,
        paddingBottom: Int?,
        actionText: String?,
        action: (() -> Unit)?
    ) {
        BaseToast()
            .init(
                mBinding.root,
                typeMessage,
                message,
                duration ?: Toast.LENGTH_SHORT,
                paddingBottom,
                actionText,
                action
            )
            .show()
    }

    protected fun isHasPermission(permissions: MutableList<String>): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permissions.all { singlePermission ->
                mBinding.root.context.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED
            }
        else true
    }

    protected fun askPermission(vararg permissions: String, @androidx.annotation.IntRange(from = 0) requestCode: Int) =
        ActivityCompat.requestPermissions(activity as Activity, permissions, requestCode)

    protected fun onRunPermission(permissions: MutableList<String>,
                                  listenerGranted: (() -> Unit)?=null,
                                  listenerDeny: (() -> Unit)?=null) {
        Timber.i("${getTagName()} onRunPermission() called")
        activity?.let {
            val view = it.findViewById<View>(android.R.id.content)
            Dexter.withActivity(it)
                .withPermissions(permissions)
                .withListener(
                    CompositeMultiplePermissionsListener(
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                            .with(view, R.string.base_permission_title)
                            .withOpenSettingsButton(R.string.base_permission_btn_text)
                            .withDuration(Snackbar.LENGTH_INDEFINITE)
                            .build(),
                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
                            .withContext(mBinding.root.context)
                            .withTitle(R.string.base_permission_title)
                            .withMessage(R.string.base_permission_message)
                            .withButtonText(android.R.string.ok)
//                        .withIcon(R.mipmap.ic_logo)
                            .build(),
                        object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                report?.let { multiplePermissionReport ->
                                    if (multiplePermissionReport.areAllPermissionsGranted()) {
                                        Timber.i("${getTagName()} AllPermissionGranted called")
                                        listenerGranted?.invoke()
                                    } else {
                                        Timber.i("${getTagName()} DenyPermission called")
                                        listenerDeny?.invoke()
                                    }
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                            ) {
                                token?.continuePermissionRequest()
                            }
                        }
                    )
                ).onSameThread().check()
        }
    }
}