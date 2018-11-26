package hu.sztomek.buxassignment.device.di

import android.app.Application
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import hu.sztomek.buxassignment.device.ResourceHelperImpl
import hu.sztomek.buxassignment.device.scheduler.WorkSchedulersImpl
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import javax.inject.Singleton

@Module
class DeviceModule {

    @Singleton
    @Provides
    fun provideResources(application: Application): Resources {
        return application.resources
    }

    @Singleton
    @Provides
    fun provideResourceHelper(resources: Resources): ResourceHelper {
        return ResourceHelperImpl(resources)
    }

    @Singleton
    @Provides
    fun provideSchedulers(): WorkSchedulers {
        return WorkSchedulersImpl()
    }

}