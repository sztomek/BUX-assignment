package hu.sztomek.buxassignment.presentation.di.module

import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.presentation.navigation.Navigator
import hu.sztomek.buxassignment.presentation.navigation.NavigatorImpl

@Module
class NavigatorModule {

    @Provides
    fun provideNavigator(): Navigator {
        return NavigatorImpl()
    }

}