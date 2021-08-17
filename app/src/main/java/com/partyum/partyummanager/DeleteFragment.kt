package com.partyum.partyummanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.partyum.partyummanager.databinding.FragmentDeleteBinding

class DeleteFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 바인딩
        val binding = DataBindingUtil.inflate<FragmentDeleteBinding>(inflater, R.layout.fragment_delete, container, false)

        // 온 클릭 리스너 달기
        binding.btDeleteYes.setOnClickListener(Listener(true))
        binding.btDeleteNo.setOnClickListener(Listener(false))

        return binding.root
    }

    inner class Listener(private val doDelete: Boolean): View.OnClickListener {
        override fun onClick(v: View?) {
            Log.i("r", "tkr")
            parentFragmentManager.setFragmentResult("DELETE", bundleOf("bundleKey" to doDelete))
            dismiss()
        }
    }
}
