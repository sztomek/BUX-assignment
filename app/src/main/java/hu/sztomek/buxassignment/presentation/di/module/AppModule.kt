package hu.sztomek.buxassignment.presentation.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.presentation.app.BUXAssignmentApplication

@Module
class AppModule {

    @Provides
    fun provideAppContext(application: BUXAssignmentApplication): Context {
        return application
    }

}