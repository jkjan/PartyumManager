package com.partyum.partyummanager.utiil

import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.partyum.partyummanager.model.PhoneFormattingTextWatcher

@BindingAdapter("android:listener")
fun addListener(view: EditText, str: String) {
    view.addTextChangedListener(PhoneFormattingTextWatcher(view))
    view.addTextChangedListener(PhoneNumberFormattingTextWatcher())
}