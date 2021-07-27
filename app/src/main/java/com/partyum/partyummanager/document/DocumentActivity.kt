package com.partyum.partyummanager.document

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.FileProvider
import com.partyum.partyummanager.R
import com.partyum.partyummanager.base.BaseActivity
import com.partyum.partyummanager.databinding.ActivityDocumentBinding
import com.partyum.partyummanager.model.MainModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class DocumentActivity : BaseActivity<ActivityDocumentBinding>() {
    override val viewModel: DocumentViewModel by viewModel()
    override val layoutResourceId: Int
        get() = R.layout.activity_document

    override fun onCreate(savedInstanceState: Bundle?) {
        val keys = intent.getSerializableExtra(getString(R.string.reservation_to_document_intent)) as Pair<String, String>

        Log.i("document activity", "예약 키 ${keys.first}, 문서 키 ${keys.second}를 넘겨받았습니다.")

        // 뷰 모델에 문서 정보 등록
        viewModel.initDocument(keys.first, keys.second)

        super.onCreate(savedInstanceState)
    }

    override fun bindData() {
        // 뷰 바인딩
        binding.viewModel = viewModel
    }

    override fun setListeners() {
        // 예약 정보 동기화
        viewModel.getInfoFromDB().observe(this, { info ->
            if (info != null) {
                viewModel.showSnackBar(getString(R.string.data_retrieved))
            }
        })

        // 문서 정보 동기화
        viewModel.getDocumentFromDB().observe(this, { document ->
            if (document != null) {
                // 뷰 스텁 (문서 부분) 이 아직 살아있을 경우 (뷰 스텁에 다른 레이아웃 안 들어간 경우)
                if (binding.documentForm.viewStub != null) {
                    // 문서 타입에 따라 문서 가져옴
                    val layoutResource = viewModel.getDocumentLayout()

                    // 이상한 값 아니라면
                    if (layoutResource != -1) {
                        // 뷰 스텁에 레이아웃 채워넣기
                        binding.documentForm.viewStub!!.layoutResource = layoutResource
                        binding.documentForm.viewStub!!.inflate()
                    }
                }

                // 문서 복원하기
                viewModel.restoreDocument(binding.documentForm.root, packageName, resources)
                viewModel.showSnackBar(R.string.data_retrieved)
            }
        })

        // 뷰 모델이 보낸 커맨드 리스너
        viewModel.command.observe(this, {
            if (it != null) {
                when (it) {
                    // 문서 DB 로 올리기
                    Command.SAVE_TO_FIREBASE -> {
                        viewModel.uploadDocumentToDB(binding.documentForm.root, packageName)
                    }

                    // 문서 PDF 로 내보내기
                    Command.EXPORT -> {
                        viewModel.uploadDocumentToDB(binding.documentForm.root, packageName)

                        // 문서이름_현재시간.pdf
                        val documentName = viewModel.document.value!!.name
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
//                            shareIntent.type = "application/pdf"

//                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

                            // 공유 액티비티 시작
                            startActivity(Intent.createChooser(shareIntent, "PDF 공유"))
                        }
                    }
                }
            }
        })
    }
}