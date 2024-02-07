package com.duakelinci.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import timber.log.Timber

abstract class BaseVM : ViewModel() {
    abstract fun getTagName(): String
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Timber.i("${getTagName()} onCleared() called")
    }
}

abstract class BaseVMWithApp(application: Application): AndroidViewModel(application){
    abstract fun getTagName(): String
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        Timber.i("${getTagName()} onCleared() called")
    }
}
