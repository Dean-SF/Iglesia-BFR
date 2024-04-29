package com.iglesiabfr.iglesiabfrnaranjo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable

class SharedViewModel : ViewModel(), Serializable {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    fun setEmail(email: String) {
        _email.value = email
    }

    fun getEmail(): String? {
        return _email.value
    }
}
