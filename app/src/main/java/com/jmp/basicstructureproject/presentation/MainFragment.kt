package com.jmp.basicstructureproject.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jmp.basicstructureproject.AppApplication

class MainFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onAttach(context: Context) {
        (requireActivity().application as AppApplication)
            .appComponent
            .mainComponent()
            .create()
            .inject(this)

        super.onAttach(context)
    }
}