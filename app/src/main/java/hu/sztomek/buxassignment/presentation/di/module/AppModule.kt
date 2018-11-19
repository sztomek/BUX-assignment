package hu.sztomek.buxassignment.presentation.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.presentation.app.BUXAssignmentApplication

@Module
class AppModule {

    @Provides
    fun provideApplication(application: BUXAssignmentApplication): Application {
        return application
    }

}