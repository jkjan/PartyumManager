package com.partyum.partyummanager.model

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneFormattingTextWatcher(private val view: EditText): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().contains(Regex("^.*[^0-9-].*\$"))) {
            view.removeTextChangedListener(this)
            view.text.clear()
            view.addTextChangedListener(this)
        }
    }
}