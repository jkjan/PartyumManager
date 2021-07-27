package com.partyum.partyummanager.reservation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.databinding.ActivityReservationBinding
import com.partyum.partyummanager.document.DocumentActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReservationActivity : BaseActivity<ActivityReservationBinding>() {
    override val viewModel: ReservationViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_reservation
    var fragment: NewDocumentFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val reservationKey = intent.getStringExtra(getString(R.string.main_to_reservation_intent))

        Log.i("reservation activity", "예약 키 " + reservationKey + "를 넘겨받았습니다.")

        if (reservationKey != null) {
            viewModel.reservationKey = reservationKey
        }

        super.onCreate(savedInstanceState)

        // 예약의 문서 리스트를 리사이클러뷰에 띄움
        val adapter = DocumentsRecyclerViewAdapter(viewModel)
        binding.rvDocs.setHasFixedSize(true)
        binding.rvDocs.layoutManager = LinearLayoutManager(this)
        binding.rvDocs.adapter = adapter
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        // 예약 데이터 동기화
        viewModel.getInfoFromDB().observe(this, {
            if (it != null) {
                viewModel.showSnackBar(R.string.data_retrieved)
            }
        })

        // 문서 데이터 동기화
        viewModel.getDocumentsFromDB().observe(this, {
            if (it != null) {
                if (binding.rvDocs.adapter != null) {
                    viewModel.showSnackBar(R.string.data_retrieved)

                    // 리사이클러뷰 업데이트
                    binding.rvDocs.adapter!!.notifyDataSetChanged()
                }
            }
        })

        // 뷰 모델에서 보낸 커맨드 리스너
        viewModel.command.observe(this, {
            if (it != null) {
                when (it) {
                    // 새 문서 추가 요청 들어옴
                    Command.ADD_NEW_DOCUMENT -> {
                        // 새 문서 선택하는 다이얼로그 띄우기
                        fragment = NewDocumentFragment()
                        fragment!!.show(supportFragmentManager, fragment!!.tag)
                    }
                }
            }
        })

        // 문서가 선택됨
        viewModel.selectedDocumentKey.observe(this, { documentKey ->
            if (documentKey != null) {
                goToDocumentActivity(documentKey)
            }
        })

        // 새 문서가 추가됨
        viewModel.newDocumentKey.observe(this, { newDocumentKey ->
            if (newDocumentKey != null) {
                // 다이얼로그 닫고 종료
                if (fragment != null) {
                    fragment!!.dismiss()
                    fragment = null

                    goToDocumentActivity(newDocumentKey)
                }
            }
        })
    }

    fun goToDocumentActivity(documentKey: String) {
        val intent = Intent(this, DocumentActivity::class.java)
        intent.putExtra(getString(R.string.reservation_to_document_intent), Pair(viewModel.reservationKey, documentKey))
        startActivity(intent)
    }
}