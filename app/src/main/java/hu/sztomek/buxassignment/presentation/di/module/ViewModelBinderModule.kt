package hu.sztomek.buxassignment.presentation.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import hu.sztomek.buxassignment.presentation.di.BUXAssignmentViewModelFactory
import hu.sztomek.buxassignment.presentation.di.ViewModelKey
import hu.sztomek.buxassignment.presentation.screen.details.ProductDetailsViewModel
import hu.sztomek.buxassignment.presentation.screen.productselect.ProductSelectViewModel

@Module
interface ViewModelBinderModule {

    @Binds
    fun bindViewModelFactory(factory: BUXAssignmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(ProductSelectViewModel::class)
    fun bindProductSelectViewModel(viewModel: ProductSelectViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProductDetailsViewModel::class)
    fun bindProductDetailsViewModel(viewModel: ProductDetailsViewModel): ViewModel

}