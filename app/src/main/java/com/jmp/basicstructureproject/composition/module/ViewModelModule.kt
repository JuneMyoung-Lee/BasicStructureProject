package com.jmp.basicstructureproject.composition.module

import androidx.lifecycle.ViewModel
import com.jmp.basicstructureproject.composition.annotation.ViewModelKey
import com.jmp.basicstructureproject.presentation.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel
}