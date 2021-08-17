package com.partyum.partyummanager.utiil

import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.partyum.partyummanager.document.DocumentViewModel
import com.partyum.partyummanager.model.PhoneFormattingTextWatcher

@BindingAdapter("android:listener")
fun addListener(view: EditText, str: String) {
    view.addTextChangedListener(PhoneFormattingTextWatcher(view))
    view.addTextChangedListener(PhoneNumberFormattingTextWatcher())
}

@BindingAdapter("android:autoSaver")
fun addAutoSaver(view: EditText, viewModel: DocumentViewModel) {
    var previousText: String? = null

    view.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
            if (view.text.isNotBlank()) {
                previousText = view.text.toString()
            }
        }
        else {
            val modifiedString = view.text.toString()
            val tag = view.tag.toString()

            if (tag.isNotEmpty()) {
                viewModel.autoSaveEditableString(tag, previousText, modifiedString)
            }
        }
    }

//    view.addTextChangedListener(AutoSavingTextWatcher(view, viewModel))
}