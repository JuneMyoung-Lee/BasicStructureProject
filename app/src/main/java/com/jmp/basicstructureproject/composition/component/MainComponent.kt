package com.jmp.basicstructureproject.composition.component

import com.jmp.basicstructureproject.presentation.MainFragment
import dagger.Subcomponent

@Subcomponent
interface MainComponent {
    fun inject(fragment: MainFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }
}