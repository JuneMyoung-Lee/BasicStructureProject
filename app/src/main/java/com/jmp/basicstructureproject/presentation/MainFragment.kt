package com.jmp.basicstructureproject.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jmp.basicstructureproject.AppApplication
import com.jmp.basicstructureproject.R
import com.jmp.basicstructureproject.databinding.FragmentMainBinding
import com.jmp.basicstructureproject.presentation.extension.viewBinding

class MainFragment: Fragment(R.layout.fragment_main) {

    private val binding by viewBinding (FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Welcome to MainFragment", Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        (requireActivity().application as AppApplication)
            .appComponent
            .mainComponent()
            .create()
            .inject(this)

        super.onAttach(context)
    }

    companion object {
        fun newInstance(bundle: Bundle?): MainFragment {
            schemeBundle = bundle
            return MainFragment()
        }

        private var schemeBundle: Bundle? = null
    }
}