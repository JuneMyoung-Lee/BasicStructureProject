package com.jmp.basicstructureproject.composition

import com.jmp.basicstructureproject.AppApplication
import com.jmp.basicstructureproject.MainActivity
import com.jmp.basicstructureproject.composition.component.MainComponent
import com.jmp.basicstructureproject.composition.module.ServiceModule
import com.jmp.basicstructureproject.composition.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelModule::class, ServiceModule::class
    ]
)
interface AppComponent {
    fun mainComponent(): MainComponent.Factory

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun bindsApp(app: AppApplication): Builder

        fun build(): AppComponent
    }
}