package com.duakelinci.cores.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.duakelinci.core.BaseActivityViewBinding
import com.duakelinci.core.R
import com.duakelinci.core.data.model.TypeMessage
import com.duakelinci.cores.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BaseActivityViewBinding<ActivityMainBinding>() {
    override fun getTagName(): String = javaClass.simpleName

    override fun setBinding(inflater: LayoutInflater): ActivityMainBinding =
        ActivityMainBinding.inflate(inflater)

    override fun onCreateUI(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())

        mBinding.btn1.setOnClickListener {
            showSnackbarMessage(TypeMessage.OTHER, "Testing snackbar message", actionText = "Cek Data") {
                dismissSnackbarMessage()
                Timber.d("showSnackbar Testing")
            }
        }

        mBinding.btn2.setOnClickListener {
            showToastMessage(TypeMessage.OTHER, "Testing toast message") {
                Timber.d("showToastMessage Testing")
            }
        }

        mBinding.btn3.setOnClickListener {
            showAlertDialog("Alert", "Testing alert dialog", actionText = "Submit"){
                hideDialog()
                Timber.d("showAlertDialog Testing")
            }
        }

        mBinding.btn4.setOnClickListener {
            showFullscreenAlertDialog("Testing fullscreen alert dialog", actionText = getString(R.string.base_btn_ok)) {
                hideDialog()
                Timber.d("showFullscreenAlertDialog Testing")
            }
        }

        mBinding.btn5.setOnClickListener {
            showLoadingDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                hideDialog()
            }, 2000)
        }
    }
}