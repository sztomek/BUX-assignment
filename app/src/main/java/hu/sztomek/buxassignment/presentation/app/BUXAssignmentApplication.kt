package hu.sztomek.buxassignment.presentation.app

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class BUXAssignmentApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

}