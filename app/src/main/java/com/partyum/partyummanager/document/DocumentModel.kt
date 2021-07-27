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
        fun getAllChildrenOfViewGroupWhere(vg: ViewGroup, condition: (View)->(Boolean)): List<String> {
            val childIds = arrayListOf<String>()

            // 뷰 그룹 밑의 모든 차일드에 대해서
            for (i in 0..vg.childCount) {
                val child = vg.getChildAt(i)
                if (child != null) {
                    // 뷰가 null 이 아니면서 명시된 id 가 존재하고
                    if (child.id != View.NO_ID) {
                        // 조건에 맞는 뷰라면 추가
                        if (condition(child)) {
                            childIds.add(getIdString(child))
                            Log.i(
                                "document activity",
                                "id=${child.resources.getResourceName(child.id)}"
                            )
                        }
                    }

                    // 밑으로 더 내려갈 수 있는 뷰라면 재귀호출
                    try {
                        val innerVg = child as ViewGroup
                        childIds.addAll(getAllChildrenOfViewGroupWhere(innerVg, condition))
                    } catch (e: ClassCastException) {
                        continue
                    }
                }
            }

            return childIds
        }

        // 작성된 문서를 해시맵으로 만들어 반환
        fun getDocumentElem(v: View, packageName: String, selectedStringSet: Set<String>): HashMap<String, String> {
            val elemIds = getAllChildrenOfViewGroupWhere(v as ViewGroup
            ) { view: View -> view is EditText }

            val documentElem = HashMap<String, String>()

            elemIds.forEach { id ->
                val idStringToInt = v.resources.getIdentifier(id, "id", packageName)
                val et = v.findViewById<EditText>(idStringToInt)
                documentElem[id] = et.text.toString()
                et.clearFocus()
            }

            var selected = ""
            selectedStringSet.forEach { tv ->
                selected += "$tv|"
            }

            documentElem["selected"] = selected

            Log.i("document model", documentElem.toString())

            return documentElem
        }
    }
}