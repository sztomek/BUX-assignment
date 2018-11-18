package hu.sztomek.buxassignment.presentation.di.module

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import hu.sztomek.buxassignment.presentation.di.BUXAssignmentViewModelFactory

@Module
interface ViewModelBinderModule {

    @Binds
    fun bindViewModelFactory(factory: BUXAssignmentViewModelFactory): ViewModelProvider.Factory
}