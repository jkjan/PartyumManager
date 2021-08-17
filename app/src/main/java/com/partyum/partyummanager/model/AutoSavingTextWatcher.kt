package com.partyum.partyummanager.model

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.partyum.partyummanager.document.DocumentViewModel

class AutoSavingTextWatcher(private val view: EditText, private val viewModel: DocumentViewModel): TextWatcher {
    var previousString: String? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        previousString = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        Log.i("${view.tag}", "$previousString -> ${s.toString()}")
    }
}