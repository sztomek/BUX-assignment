package hu.sztomek.buxassignment.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class BUXAssignmentViewModelFactory @Inject constructor(private var providerMap: Map<Class<out ViewModel>,
        @JvmSuppressWildcards Provider<ViewModel>>)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var provider = providerMap[modelClass]
        if (provider == null) {
            for (entry: Map.Entry<Class<out ViewModel>, Provider<ViewModel>> in providerMap.entries) {
                if (modelClass.isAssignableFrom(entry.key)) {
                    provider = entry.value
                    break
                }
            }
        }
        if (provider == null) {
            throw IllegalArgumentException("Unknown model class $modelClass")
        }

        return provider.get() as T
    }
}