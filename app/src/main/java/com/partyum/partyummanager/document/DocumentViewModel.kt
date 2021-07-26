package com.partyum.partyummanager.document

import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.model.MainModel


class DocumentViewModel(private val documentRepository: DocumentRepository, private val mainModel: MainModel): BaseViewModel() {
    lateinit var document: MutableLiveData<Document>
    lateinit var reservationKey: String
    lateinit var documentKey: String
    lateinit var selectedString: MutableLiveData<TextView>
    lateinit var selectedStringSet: Set<String>
    lateinit var command: MutableLiveData<Command>

    override fun init() {
        document = MutableLiveData(null)
        selectedString = MutableLiveData(null)
        command = MutableLiveData(null)
        selectedStringSet = setOf()
    }

    fun initDocument(reservationKey: String, documentKey: String) {
        this.reservationKey = reservationKey
        this.documentKey = documentKey
    }

    fun getDocumentFromDB(): LiveData<Document> {
        documentRepository.getDocumentFromDB(reservationKey, documentKey, document)
        return document
    }

    fun getNowToString(): String {
        return mainModel.getNowToString("yyyy년 M월 d일")
    }

    fun selectString(view: View) {
        selectedString.value = view as TextView
    }

    fun saveDocument(command: Command) {
        this.command.value = command
    }

    fun declareIsPhoneNumber(view: View): TextView {
        val tv = view as TextView
        return tv
    }
}