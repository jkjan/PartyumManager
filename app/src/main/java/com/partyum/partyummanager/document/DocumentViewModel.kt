package com.partyum.partyummanager.document

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseViewModel
import com.partyum.partyummanager.dao.Document
import com.partyum.partyummanager.dao.Info
import com.partyum.partyummanager.model.PdfModel
import com.partyum.partyummanager.repository.DocumentRepository
import com.partyum.partyummanager.repository.ReservationRepository
import java.io.File


class DocumentViewModel(private val reservationRepository: ReservationRepository, private val documentRepository: DocumentRepository): BaseViewModel() {
    lateinit var info: MutableLiveData<Info>
    lateinit var document: MutableLiveData<Document>
    lateinit var reservationKey: String
    lateinit var documentKey: String
    lateinit var selectedStringSet: Set<String>
    lateinit var command: MutableLiveData<Command>

    override fun init() {
        info = MutableLiveData(null)
        document = MutableLiveData(null)
        command = MutableLiveData(null)
        selectedStringSet = setOf()
    }

    // 예약, 문서 키 설정
    fun initDocument(reservationKey: String, documentKey: String) {
        this.reservationKey = reservationKey
        this.documentKey = documentKey
    }

    // 문서 레이아웃 얻어오기
    fun getDocumentLayout(): Int {
        return when (document.value!!.type) {
            // 상담 카드
            "CSC"-> R.layout.form_consulting_card

            // 예약 안내서
            "EST"-> R.layout.form_estimate_sheet

            // 약관 안내
            "TOS"-> R.layout.form_terms_of_service

            // 계약서
            "CTR"-> R.layout.form_contract

            // 계산서
            "STM"-> R.layout.form_statement

            // 이상한 값
            else-> -1
        }
    }

    // 문서 데이터 동기화
    fun getDocumentFromDB(): LiveData<Document> {
        documentRepository.getDocumentFromDB(reservationKey, documentKey, document)
        return document
    }

    // 예약 데이터 동기화
    fun getInfoFromDB(): LiveData<Info> {
        reservationRepository.getReservation(reservationKey, info)
        return info
    }

    // 액티비티로 커맨드 보내기
    fun onClick(command: Command) {
        this.command.value = command
    }

    // 사용자 터치로 텍스트 뷰가 선택됨
    fun selectString(view: View) {
        val tv = view as TextView
        // 이미 선택된 것일 경우 제거하고 마킹 없애기
        if (selectedStringSet.contains(DocumentModel.getIdString(tv))) {
            selectedStringSet = selectedStringSet.minus(DocumentModel.getIdString(tv))
            unmarkString(tv)
        }
        // 기존 셋에 없으면 추가하고 마킹하기
        else {
            selectedStringSet = selectedStringSet.plus(DocumentModel.getIdString(tv))
            markString(tv)
        }
    }

    // 텍스트 뷰 마킹: 빨간색으로 바꾸고 굵게
    fun markString(tv: TextView) {
        Log.i("document viewmodel", "${DocumentModel.getIdString(tv)}를 체크합니다.")
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.setTextColor(Color.RED)
    }

    // 텍스트 뷰 마킹 풀기: 글자 검게 하고 굵기 원래대로
    fun unmarkString(tv: TextView) {
        Log.i("document viewmodel", "${DocumentModel.getIdString(tv)}를 언체크합니다.")
        tv.typeface = Typeface.DEFAULT
        tv.setTextColor(Color.BLACK)
    }

    // 문서 복원
    fun restoreDocument(view: View, packageName: String, resources: Resources) {
        // 선택된 텍스트 뷰 초기화
        selectedStringSet = setOf()

        // DB 에서 가져온 문서에서
        document.value!!.elem.forEach { entry ->
            // 선택 태그 아닌 경우
            if (entry.key != "selected") {
                // 문자열 id 로 정수 id 반환
                val id = resources.getIdentifier(entry.key, "id", packageName)

                // EditText 에 값을 채워넣음
                view.rootView.findViewById<EditText>(id)?.setText(entry.value)
            }
            // 선택 태그인 경우
            else {
                // '|' 기준으로 나눔
                val selected = entry.value.split("|")

                // DB 에 있던 것들 토대로 셋에 추가
                selected.forEach { selectedStringId ->
                    selectedStringSet = selectedStringSet.plus(selectedStringId)
                }
            }
        }

        // 선택 가능한 문자열 복원하기
        restoreSelectableStrings(view as ViewGroup, packageName, resources)
    }

    // 문서 데이터 받아온 뒤 선택 가능한 텍스트 뷰를 다시 DB 대로 마킹/언마킹 하기
    fun restoreSelectableStrings(vg: ViewGroup, packageName: String, resources: Resources) {
        // 텍스트 뷰이면서 에딧텍스트가 아니고 클릭 가능한 모든 뷰의 id
        val selectableStringIds = DocumentModel.getAllChildrenOfViewGroupWhere(vg) { view: View ->
            view is TextView && view !is EditText && view.isClickable
        }

        // 선택 가능한 텍스트 뷰의 모든 id 에 대해서
        selectableStringIds.forEach { stringId->
            // 집합에 있다면 마킹하기
            val intId = resources.getIdentifier(stringId, "id", packageName)
            val view = vg.findViewById<TextView>(intId)
            if (selectedStringSet.contains(stringId)) {
                Log.i("document viewmodel", "${stringId}를 마킹합니다.")
                markString(view)
            }
            // 없다면 언마킹하기
            else {
                Log.i("document viewmodel", "${stringId}를 언마킹합니다.")
                unmarkString(view)
            }
        }
    }

    // 문서 DB에 업로드 하기
    fun uploadDocumentToDB(view: View, packageName: String) {
        // 문서에 있는 텍스트 뷰, 에딧텍스트 저장해서 해시맵으로 만들기
        val documentElem = DocumentModel.getDocumentElem(view, packageName, selectedStringSet)

        // 예약 키, 문서 키로 해시맵 저장
        documentRepository.saveDocumentToDB(reservationKey, documentKey, documentElem)
    }

    // PDF 생성
    fun generatePdf(view: View, savePath: String): File? {
        return PdfModel.generatePdf(view, savePath)
    }
}