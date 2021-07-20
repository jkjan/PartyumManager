package com.partyum.partyummanager.reservation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.dao.Reservation
import com.partyum.partyummanager.databinding.ActivityReservationBinding
import com.partyum.partyummanager.document.DocumentActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: 예약 선택 후 문서 리스트에 대해서 데이터 동기화하기
class ReservationActivity : BaseActivity<ActivityReservationBinding>() {
    override val viewModel: ReservationViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_reservation
    private var fragment: ReservationSelectFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main Activity 에서 보낸 예약을 받음
        val reservations = intent.getSerializableExtra(getString(R.string.main_to_reservation_intent)) as List<Reservation>

        // 예약 로그
        reservations.forEach {
            Log.i("reservation activity", it.toString())
        }

        // 예약이 1건 이상일 시, 다이얼로그 띄워 선택하게 함
        if (reservations.size > 1) {
            Log.i("reservation activity", "검색된 예약건이 하나 이상입니다.")
            viewModel.initReservations(reservations)
            fragment = ReservationSelectFragment()
            fragment!!.isCancelable = false
            fragment!!.show(supportFragmentManager, fragment!!.tag)
        }

        // 딱 1건이면 바로 진행함
        else if (reservations.size == 1) {
            Log.i("reservation activity", "검색된 예약건이 하나입니다.")
            viewModel.selectedReservation.value = reservations[0]
        }
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        viewModel.selectedReservation.observe(this, {
            // 사용할 예약건이 선택됨
            if (it != null) {
                // 프래그먼트가 null 이 아니라면 닫고 예약들과 프래그먼트를 삭제함
                if (fragment != null) {
                    fragment!!.dismiss()
                    fragment = null
                    viewModel.reservations = null
                }

                // 선택된 예약의 문서 리스트를 리사이클러뷰에 띄움
                val adapter = DocumentsRecyclerViewAdapter(viewModel)
                binding.rvDocs.setHasFixedSize(true)
                binding.rvDocs.layoutManager = LinearLayoutManager(this)
                binding.rvDocs.adapter = adapter
            }
        })

        viewModel.selectedDocumentIndex.observe(this, {
            // 문서가 선택됨
            if (it != -1) {
                val document = viewModel.selectedReservation.value!!.docs[viewModel.selectedDocumentIndex.value!!]
                val intent = Intent(this, DocumentActivity::class.java)
                intent.putExtra(getString(R.string.reservation_to_document_intent), document)
                startActivity(intent)
            }
        })
    }
}