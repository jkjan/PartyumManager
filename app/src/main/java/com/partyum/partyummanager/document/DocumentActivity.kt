package com.partyum.partyummanager.document

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.partyum.partyummanager.DeleteFragment
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.base.Command
import com.partyum.partyummanager.databinding.ActivityDocumentBinding
import com.partyum.partyummanager.model.MainModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DocumentActivity : BaseActivity<ActivityDocumentBinding>(), PopupMenu.OnMenuItemClickListener {
    override val viewModel: DocumentViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_document
    var modifyDocumentFragment: ModifyDocumentFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val keys = intent.getSerializableExtra(getString(R.string.reservation_to_document_intent)) as Pair<*, *>

        Log.i("document activity", "예약 키 ${keys.first}, 문서 키 ${keys.second}를 넘겨받았습니다.")

        // 뷰 모델에 문서 정보 등록
        viewModel.initDocument(keys.first as String, keys.second as String)

        super.onCreate(savedInstanceState)

        binding.documentFormPreview.startShimmerAnimation()
        binding.documentTitlePreview.startShimmerAnimation()
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        // 예약 정보 동기화
        viewModel.getReservationInfoFromDB().observe(this, { info ->
            if (info != null) {
                viewModel.showSnackBar(getString(R.string.data_retrieved))
            }
        })

        // 문서 정보 동기화
        viewModel.getDocumentInfoFromDB().observe(this, { documentInfo ->
            if (documentInfo != null) {
                Log.i("ㅎㅇ", "문서 정보= $documentInfo")

                // 뷰 스텁 (문서 부분) 이 아직 살아있을 경우 (뷰 스텁에 다른 레이아웃 안 들어간 경우)
                if (binding.documentForm.viewStub != null) {
                    // 모든 레이아웃 리소스 객체
                    val fields = R.layout::class.java.fields

                    // 리소스 객체들을 id 와 이름으로 나누기
                    val layoutIdAndNames = fields.map { field->
                        Pair(field.getInt(this), resources.getResourceEntryName(field.getInt(this)))
                    }

                    // 레이아웃 선택
                    val form = viewModel.getDocumentLayout(layoutIdAndNames)

                    // 레이아웃이 존재, 유효한 문서임
                    if (form != null) {
                        // 뷰 스텁에 레이아웃 넣기
                        binding.documentForm.viewStub!!.layoutResource = form
                        binding.documentForm.viewStub!!.inflate()

                        // 프리뷰 이미지 중지하고 뷰 전환하기
                        binding.documentFormPreview.stopShimmerAnimation()
                        binding.documentTitlePreview.stopShimmerAnimation()
                        binding.documentFormPreviewSwitcher.showNext()
                        binding.documentTitlePreviewSwitcher.showNext()

                        // 팝업 메뉴에 클릭 리스너 달기
                        binding.ivDocumentMore.setOnClickListener {
                            val popup = PopupMenu(this, it)
                            popup.setOnMenuItemClickListener(this)
                            popup.inflate(R.menu.menu_document)
                            popup.show()
                        }
                    }
                }

                if (binding.documentForm.root != null) {
                    viewModel.getSelectableValues().observe(this, {
                        viewModel.restoreSelectableValues(binding.documentForm.root, resources, packageName)
                    })

                    viewModel.getEditableValues().observe(this, {
                        viewModel.restoreEditableValues(binding.documentForm.root, resources, packageName)
                    })

                    viewModel.getImageURLs().observe(this, {
                        viewModel.restoreImageURLs(binding.documentForm.root, resources, packageName, this)
                    })
                }

                // 문서 생성 일자
                val visitedDate = binding.documentForm.root.findViewById<TextView>(resources.getIdentifier("tv_csc_visit_date", "id", packageName))

                if (visitedDate != null) {
                    visitedDate.text = viewModel.documentInfo.value!!.createdDateTime
                }
            }
        })

        // 뷰 모델이 보낸 커맨드 리스너
        viewModel.command.observe(this, {
            if (it != null) {
                when (it) {
                    Command.VIEW_HISTORY -> {

                    }

                    Command.MODIFY_DOCUMENT -> {
                        viewModel.initDocumentModifying()
                        modifyDocumentFragment = ModifyDocumentFragment()
                        modifyDocumentFragment!!.show(supportFragmentManager, modifyDocumentFragment!!.tag)
                    }

                    // 문서 DB 로 올리기
                    Command.SAVE_TO_DATABASE -> {
//                        viewModel.uploadDocumentToDB(binding.documentForm.root, packageName)
                    }

                    // 문서 PDF 로 내보내기
                    Command.EXPORT -> {
//                        viewModel.uploadDocumentToDB(binding.documentForm.root, packageName)
                        // 문서이름_현재시간.pdf
                        val documentName = viewModel.documentInfo.value!!.name
                        val now = MainModel.getNowToString(getString(R.string.date_format_for_file))
                        val fileName = "${documentName}_$now.pdf"

                        // PDF 생성
                        val pdf = viewModel.generatePdf(binding.documentForm.root, this.cacheDir.absolutePath + File.separator + fileName)

                        // PDF 생성에 성공했을 경우
                        if (pdf != null) {
                            // 공유할 URI 생성
                            val contentUri: Uri = FileProvider.getUriForFile(
                                applicationContext,
                                applicationContext.packageName + ".fileprovider",
                                pdf
                            )

                            // 공유 인텐트 생성
                            val shareIntent = Intent(Intent.ACTION_VIEW)

                            // 인텐트에 URI 읽기 권한 부여
                            shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                            // 인텐트에 공유할 데이터와 타입 설정
                            shareIntent.setDataAndType(contentUri, "application/pdf")

                            // 공유 액티비티 시작
                            startActivity(Intent.createChooser(shareIntent, "PDF 공유"))
                        }
                    }

                    // 문서 삭제
                    Command.DELETE_DOCUMENT-> {
                        val fragment = DeleteFragment()
                        fragment.isCancelable = false

                        supportFragmentManager.setFragmentResultListener("DELETE", this) { _, bundle ->
                            val result = bundle.getBoolean("bundleKey")

                            if (result) {
                                viewModel.deleteDocument()
                                finish()
                            }
                        }
                        fragment.show(supportFragmentManager, fragment.tag)

                    }

                    Command.CLOSE_FRAGMENT-> {
                        if (modifyDocumentFragment != null) {
                            modifyDocumentFragment!!.dismiss()
                            modifyDocumentFragment = null
                        }
                    }

                    else -> {}
                }
            }
        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.document_view_history-> {viewModel.command.postValue(Command.VIEW_HISTORY);true
            }
            R.id.document_export-> {viewModel.command.postValue(Command.EXPORT); true
            }
            R.id.document_modify-> {viewModel.command.postValue(Command.MODIFY_DOCUMENT); true
            }
            R.id.document_send_to_db-> {viewModel.command.postValue(Command.SAVE_TO_DATABASE); true
            }
            R.id.document_delete-> {viewModel.command.postValue(Command.DELETE_DOCUMENT); true
            }
            else->
                false
        }
    }
}