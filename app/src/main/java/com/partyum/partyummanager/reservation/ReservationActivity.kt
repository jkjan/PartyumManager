package com.partyum.partyummanager.reservation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.partyum.partyummanager.DeleteFragment
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.databinding.ActivityReservationBinding
import com.partyum.partyummanager.document.DocumentActivity
import com.partyum.partyummanager.main.NewReservationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReservationActivity : BaseActivity<ActivityReservationBinding>(), PopupMenu.OnMenuItemClickListener {
    override val viewModel: ReservationViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_reservation
    private var newDocumentFragment: NewDocumentFragment? = null
    private var modifyReservationFragment: ModifyReservationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val reservationKey = intent.getStringExtra(getString(R.string.main_to_reservation_intent))!!

        Log.i("reservation activity", "예약 키 " + reservationKey + "를 넘겨받았습니다.")

        viewModel.setReservationKey(reservationKey)

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
        viewModel.getReservationInfoFromDB().observe(this, { info->
            // 예약이 유효할 시
            if (info != null) {
                viewModel.showSnackBar(R.string.data_retrieved)
            }
        })

        binding.ivReservationMore.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.menu_reservation)
            popup.show()
        }

        // 문서 데이터 동기화
        viewModel.getDocumentsFromDB().observe(this, {
            if (binding.rvDocs.adapter != null) {
                viewModel.showSnackBar(R.string.data_retrieved)

                // 리사이클러뷰 업데이트
                binding.rvDocs.adapter!!.notifyDataSetChanged()
            }
        })

        // 뷰 모델에서 보낸 커맨드 리스너
        viewModel.command.observe(this, {
            if (it != null) {
                when (it) {
                    Command.MODIFY_RESERVATION-> {
                        viewModel.initModification()
                        modifyReservationFragment = ModifyReservationFragment()
                        modifyReservationFragment!!.show(supportFragmentManager, modifyReservationFragment!!.tag)
                    }

                    // 새 문서 추가 요청 들어옴
                    Command.ADD_NEW_DOCUMENT -> {
                        // 새 문서 선택하는 다이얼로그 띄우기
                        viewModel.initAddNewDocument()
                        newDocumentFragment = NewDocumentFragment()
                        newDocumentFragment!!.show(supportFragmentManager, newDocumentFragment!!.tag)

                        // 새 문서가 추가됨
                        viewModel.newDocumentKey.observe(this, { newDocumentKey ->
                            if (newDocumentKey != null) {
                                // 다이얼로그 닫고 종료
                                if (newDocumentFragment != null) {
                                    newDocumentFragment!!.dismiss()
                                    newDocumentFragment = null

                                    goToDocumentActivity(newDocumentKey)
                                }
                            }
                        })
                    }

                    // 예약 삭제
                    Command.DELETE_RESERVATION -> {
                        val fragment = DeleteFragment()
                        fragment.isCancelable = false

                        supportFragmentManager.setFragmentResultListener("DELETE", this) { _, bundle ->
                            val result = bundle.getBoolean("bundleKey")
                            if (result) {
                                viewModel.deleteReservation()
                                finish()
                            }
                        }
                        fragment.show(supportFragmentManager, fragment.tag)
                    }

                    Command.CLOSE_FRAGMENT-> {
                        if (newDocumentFragment != null) {
                            newDocumentFragment!!.dismiss()
                            newDocumentFragment = null
                        }
                        if (modifyReservationFragment != null) {
                            modifyReservationFragment!!.dismiss()
                            modifyReservationFragment = null
                        }
                    }

                    else -> {}
                }
            }
        })

        // 문서가 선택됨
        viewModel.selectedDocumentKey.observe(this, { documentKey ->
            if (documentKey != null) {
                goToDocumentActivity(documentKey)
            }
        })
    }

    private fun goToDocumentActivity(documentKey: String) {
        val intent = Intent(this, DocumentActivity::class.java)
        intent.putExtra(getString(R.string.reservation_to_document_intent), Pair(viewModel.getReservationKey(), documentKey))
        startActivity(intent)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.reservation_modify-> {viewModel.command.postValue(Command.MODIFY_RESERVATION); true
            }
            R.id.reservation_add_new_document-> {
                viewModel.command.postValue(Command.ADD_NEW_DOCUMENT); true
            }
            R.id.reservation_delete-> {viewModel.command.postValue(Command.DELETE_RESERVATION); true
            }
            else->
                false
        }
    }
}