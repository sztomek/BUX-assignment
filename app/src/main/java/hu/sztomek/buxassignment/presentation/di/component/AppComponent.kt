package hu.sztomek.buxassignment.presentation.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import hu.sztomek.buxassignment.data.di.DataModule
import hu.sztomek.buxassignment.device.di.DeviceModule
import hu.sztomek.buxassignment.presentation.app.BUXAssignmentApplication
import hu.sztomek.buxassignment.presentation.di.module.ActivityBinderModule
import hu.sztomek.buxassignment.presentation.di.module.AppModule
import hu.sztomek.buxassignment.presentation.di.module.NavigatorModule
import hu.sztomek.buxassignment.presentation.di.module.ViewModelBinderModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = arrayOf(
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ViewModelBinderModule::class,
        ActivityBinderModule::class,
        DataModule::class,
        DeviceModule::class,
        NavigatorModule::class
    )
)
interface AppComponent : AndroidInjector<BUXAssignmentApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: BUXAssignmentApplication): Builder

        fun build(): AppComponent
    }

}