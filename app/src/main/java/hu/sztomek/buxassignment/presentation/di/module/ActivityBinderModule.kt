package hu.sztomek.buxassignment.presentation.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import hu.sztomek.buxassignment.presentation.di.ActivityScope
import hu.sztomek.buxassignment.presentation.screen.details.ProductDetailsActivity
import hu.sztomek.buxassignment.presentation.screen.productselect.ProductSelectActivity

@Module
interface ActivityBinderModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun bindProductSelectActivity(): ProductSelectActivity

    @ActivityScope
    @ContributesAndroidInjector
    fun bindProductDetailsActivity(): ProductDetailsActivity

}