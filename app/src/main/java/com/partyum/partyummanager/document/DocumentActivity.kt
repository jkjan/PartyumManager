package com.partyum.partyummanager.document

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.databinding.ActivityDocumentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class DocumentActivity : BaseActivity<ActivityDocumentBinding>() {
    override val viewModel: DocumentViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_document

    override fun onCreate(savedInstanceState: Bundle?) {
        val keys = intent.getSerializableExtra(getString(R.string.reservation_to_document_intent)) as Pair<String, String>

        Log.i("document activity", "예약 키 ${keys.first}, 문서 키 ${keys.second}를 넘겨받았습니다.")

        viewModel.initDocument(keys.first, keys.second)

        super.onCreate(savedInstanceState)
    }

    override fun bindData() {
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        viewModel.getDocumentFromDB().observe(this, {
            if (it != null) {
                binding.documentForm.viewStub!!.layoutResource = R.layout.form_consulting_card
                binding.documentForm.viewStub!!.inflate()
                viewModel.showSnackBar(R.string.data_retrieved)
//                getAllEditableChildren(vg)
            }
        })

        viewModel.selectedString.observe(this, {
            if (it != null) {
                Log.i("document activity", it.typeface.toString())
                if (it.typeface == Typeface.DEFAULT_BOLD) {
                    it.typeface = Typeface.DEFAULT
                    it.setTextColor(getColor(R.color.black))
                    viewModel.selectedStringSet = viewModel.selectedStringSet.minus(getIdString(it))
                }
                else {
                    it.typeface = Typeface.DEFAULT_BOLD
                    it.setTextColor(getColor(R.color.red))
                    viewModel.selectedStringSet = viewModel.selectedStringSet.plus(getIdString(it))
                }
                Log.i("document activity", viewModel.selectedStringSet.toString())
            }
        })

        viewModel.command.observe(this, {
            if (it != null) {
                when (it) {
                    Command.SAVE_TO_FIREBASE -> {
                        saveDocument()
                    }
                }
            }
        })
    }

    fun getAllEditableChildren(vg: ViewGroup): List<String> {
        val elemIds = arrayListOf<String>()

        for (i in 0..vg.childCount) {
            val child = vg.getChildAt(i)
            if (child != null) {
                if (child.id != View.NO_ID) {
                    if (child is EditText) {
                        elemIds.add(getIdString(child))
                        Log.i("document activity", "id=${child.resources.getResourceName(child.id)}")
                    }
                }

                try {
                    val innerVg = child as ViewGroup
                    elemIds.addAll(getAllEditableChildren(innerVg))
                } catch (e: ClassCastException) {
                    continue
                }
            }
        }

        return elemIds
    }

    fun saveDocument() {
        val v = binding.documentForm.root
        val elemIds = getAllEditableChildren(v as ViewGroup)
        val documentElem = HashMap<String, String>()

        elemIds.forEach { id ->
            val idStringToInt = v.resources.getIdentifier(id, "id", packageName)
            val et = v.findViewById<EditText>(idStringToInt)
            documentElem[id] = et.text.toString()
        }

        var selected = ""
        viewModel.selectedStringSet.forEach { tv ->
            selected += "$tv|"
        }

        documentElem["selected"] = selected

        Log.i("document activity", documentElem.toString())
    }

    fun getIdString(view: View): String {
        return view.resources.getResourceName(view.id).split("id/")[1]
    }
}