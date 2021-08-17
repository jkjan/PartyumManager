package com.partyum.partyummanager.document

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class DocumentModel {
    companion object {
        // 뷰에서 문자열 id 를 얻어옴
        fun getIdString(view: View): String {
            return view.resources.getResourceName(view.id).split("id/")[1]
        }

        // 조건에 따라 뷰 그룹 내의 모든 차일드의 문자열 id 얻기
        fun getAllChildrenOfViewGroupWhere(vg: ViewGroup, condition: (View)->(Boolean)): List<View> {
            val documentElemViews = arrayListOf<View>()

            // 뷰 그룹 밑의 모든 차일드에 대해서
            for (i in 0..vg.childCount) {
                val child = vg.getChildAt(i)
                if (child != null) {
                    val tag = child.tag.toString()

                    // 뷰가 null 이 아니면서 명시된 tag 가 존재하면 추가
                    if (tag.isNotEmpty()) {
                        if (condition(child)) {
                            documentElemViews.add(child)
                        }
                    }

                    // 밑으로 더 내려갈 수 있는 뷰라면 재귀호출
                    try {
                        val innerVg = child as ViewGroup
                        documentElemViews.addAll(getAllChildrenOfViewGroupWhere(innerVg, condition))
                    } catch (e: ClassCastException) {
                        continue
                    }
                }
            }

            return documentElemViews
        }
//
//        // 작성된 문서를 해시맵으로 만들어 반환
//        fun getDocumentElem(v: View, packageName: String, selectedStringSet: Set<String>): HashMap<String, String> {
//            val elemIds = getAllChildrenOfViewGroupWhere(v as ViewGroup
//            ) { view: View -> view is EditText }
//
//            val documentElem = HashMap<String, String>()
//
//            elemIds.forEach { id ->
//                val idStringToInt = v.resources.getIdentifier(id, "id", packageName)
//                val et = v.findViewById<EditText>(idStringToInt)
//                documentElem[id] = et.text.toString()
//                et.clearFocus()
//            }
//
//            var selected = ""
//            selectedStringSet.forEach { tv ->
//                selected += "$tv|"
//            }
//
//            documentElem["selected"] = selected
//
//            Log.i("document model", documentElem.toString())
//
//            return documentElem
//        }
//
//        // 문서 저장 시 차이점 비교하고 히스토리 생성하는 부분
//        fun getDifference(prevElem: HashMap<String, String>, modifiedElem: HashMap<String, String>) {
//
//        }
    }
}