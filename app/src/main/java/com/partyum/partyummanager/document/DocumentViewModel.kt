package com.partyum.partyummanager.document

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.dao.DocumentInfo
import com.partyum.partyummanager.dao.ReservationInfo
import com.partyum.partyummanager.model.MainModel
import com.partyum.partyummanager.model.PdfModel
import com.partyum.partyummanager.repository.DocumentRepository
import com.partyum.partyummanager.repository.ReservationRepository
import com.squareup.picasso.Picasso
import java.io.File


class DocumentViewModel(private val reservationRepository: ReservationRepository, private val documentRepository: DocumentRepository): BaseViewModel() {
    val info = MutableLiveData<ReservationInfo>(null)
    val documentInfo = MutableLiveData<DocumentInfo>(null)
    val selectableValues = MutableLiveData<HashMap<String, Boolean>>(hashMapOf())
    val editableValues = MutableLiveData<HashMap<String, String>>(null)
    val imageURLs = MutableLiveData<HashMap<String, String>>(null)
    lateinit var invalidDate: MutableLiveData<Boolean>

    override fun init() {
    }

    // 예약, 문서 키 설정
    fun initDocument(reservationKey: String, documentKey: String) {
        documentRepository.reservationKey = reservationKey
        documentRepository.documentKey = documentKey
        documentRepository.dbFailed = dbFailed
        reservationRepository.reservationKey = reservationKey
        reservationRepository.dbFailed = dbFailed
    }

    // 문서 데이터 동기화
    fun getDocumentInfoFromDB(): LiveData<DocumentInfo> {
        documentRepository.getDocumentInfo(documentInfo)
        return documentInfo
    }

    // 예약 데이터 동기화
    fun getReservationInfoFromDB(): LiveData<ReservationInfo> {
        reservationRepository.getReservationInfo(info)
        return info
    }

    fun initDocumentModifying() {
        invalidDate = MutableLiveData(false)
    }

    // 문서 레이아웃 얻어오기
    fun getDocumentLayout(layoutResources: List<Pair<Int, String>>): Int? {
        if (documentInfo.value != null) {
            // 현재 문서의 양식 이름
            val currentForm = "form_${documentInfo.value!!.type}"

            // 문서의 생성일자를 레이아웃 파일 이름 형식에 맞춤
            val currentDate = documentInfo.value!!.createdDateTime.split(" ")[0].replace('-', '_')

            // 현재 문서의 양식과 생성일자
            val currentFormDate = "${currentForm}_${currentDate}"

            Log.i("document viewmodel", "$currentFormDate 레이아웃을 가져옵니다.")

            val documentForms = layoutResources.filter { (layoutId, layoutName) ->
                // 양식과 일치하면서
                layoutName.contains(currentForm)
                        &&
                        // 생성일보다 이전 날짜의 양식
                        layoutName <= currentFormDate
            }

            documentForms.forEach {
                Log.i("document viewmodel", it.toString())
            }

            // 가장 최근의 날짜의 양식을 선택
            return documentForms.maxByOrNull { it.second }?.first
        }
        return null
    }

    fun restoreSelectableValues(view: View, resources: Resources, packageName: String) {
        selectableValues.value?.forEach { (key, value) ->
            val id = resources.getIdentifier(key, "id", packageName)

            if (value) {
                selectString(view.findViewById(id))
            } else {
                unselectString(view.findViewById(id))
            }
        }
    }

    fun restoreEditableValues(view: View, resources: Resources, packageName: String) {
        editableValues.value?.forEach { (key, value)->
            val id = resources.getIdentifier(key, "id", packageName)
            val editText = view.findViewById<EditText>(id)
            editText.setText(value)
        }
    }

    fun restoreImageURLs(view: View, resources: Resources, packageName: String, context: Context) {
        imageURLs.value?.forEach { (key, value)->
            val id = resources.getIdentifier(key, "id", packageName)
            val imageView = view.findViewById<ImageView>(id)
            Picasso.with(context).load(value).into(imageView)
        }
    }

    // 사용자 터치로 텍스트 뷰가 선택됨
    fun toggleString(view: View) {

        val tv = view as TextView
        val tag = tv.tag.toString()

        Log.i("d", "tlqkf $tag")

        if (tag.isNotEmpty()) {
            if (!selectableValues.value!!.containsKey(tag) || !selectableValues.value!![tag]!!) {
                selectableValues.value!![tag] = true
                selectString(tv)
            }
            else {
                selectableValues.value!![tag] = false
                unselectString(tv)
            }

            documentRepository.modifyDocumentValues(tag, null, selectableValues.value!![tag]!!, "selectable")
        }
    }

    // 텍스트 뷰 선택하기: 빨간색으로 바꾸고 굵게
    private fun selectString(tv: TextView) {
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.setTextColor(Color.RED)
    }

    // 텍스트 뷰 선택 풀기: 글자 검게 하고 굵기 원래대로
    private fun unselectString(tv: TextView) {
        tv.typeface = Typeface.DEFAULT
        tv.setTextColor(Color.BLACK)
    }

    fun autoSaveEditableString(editableStringTag: String, previousString: String?, modifiedString: String) {
        documentRepository.modifyDocumentValues(editableStringTag, previousString, modifiedString, "editable")
    }
    
    // PDF 생성
    fun generatePdf(view: View, savePath: String): File? {
        return PdfModel.generatePdf(view, savePath)
    }

    fun deleteDocument() {
        documentRepository.deleteDocument()
    }

    fun modifyDocumentInfo(name: String, createdDateTime: String) {
        if (name.isNotEmpty() && createdDateTime.isNotEmpty()) {
            if (!MainModel.isValidDateTime(createdDateTime)) {
                invalidDate.value = true
            }

            else if (documentInfo.value != null) {
                documentInfo.value!!.name = name
                documentInfo.value!!.createdDateTime = createdDateTime
                documentRepository.createNewDocumentInfo(documentInfo.value!!, null)
                command.value = Command.CLOSE_FRAGMENT
            }
        }
    }

    fun getEditableValues(): LiveData<HashMap<String, String>> {
        documentRepository.getDocumentValues(editableValues, "editable")
        return editableValues
    }

    fun getSelectableValues(): LiveData<HashMap<String, Boolean>> {
        documentRepository.getDocumentValues(selectableValues, "selectable")
        return selectableValues
    }

    fun getImageURLs(): LiveData<HashMap<String, String>> {
        documentRepository.getDocumentValues(imageURLs, "image-url")
        return imageURLs
    }
}