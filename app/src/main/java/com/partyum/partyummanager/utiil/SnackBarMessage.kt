package com.partyum.partyummanager.utiil

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer


class SnackBarMessage : SingleLiveEvent<Int>() {
    fun observe(owner: LifecycleOwner, observer: (Int) -> Unit) {
        super.observe(owner, {
            it?.run {
                observer(it)
            }
        })
    }
}

class SnackBarString : SingleLiveEvent<String>() {
    fun observe(owner: LifecycleOwner, observer: (String) -> Unit) {
        super.observe(owner, {
            it?.run {
                observer(it)
            }
        })
    }
}