package com.partyum.partyummanager.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.partyum.partyummanager.utiil.SnackBarMessage
import com.partyum.partyummanager.utiil.SnackBarString

abstract class BaseViewModel: ViewModel() {
    // 일회성 이벤트를 만들어 내는 라이브 이벤트
    // 뷰는 이러한 이벤트를 바인딩하고 있다가, 적절한 상황이 되면 액티비티를 호출하거나 스낵바를 만듬
    private val snackBarMessage = SnackBarMessage()
    private val snackBarString = SnackBarString()
    val dbFailed = MutableLiveData(false)
    val command = MutableLiveData<Command>(null)

    // String ID 로 스낵바 띄우기
    fun showSnackBar(stringResourceId: Int) {
        snackBarMessage.value = stringResourceId
    }

    // String 으로 스낵바 띄우기
    fun showSnackBar(str: String) {
        snackBarString.value = str
    }

    fun observeSnackBarMessage(lifeCycleOwner: LifecycleOwner, ob: (Int) -> Unit) {
        snackBarMessage.observe(lifeCycleOwner, ob)
    }

    fun observeSnackBarMessageStr(lifeCycleOwner: LifecycleOwner, ob: (String) -> Unit) {
        snackBarString.observe(lifeCycleOwner, ob)
    }

    fun onClick(command: Command) {
        this.command.value = command
    }

    abstract fun init()
}