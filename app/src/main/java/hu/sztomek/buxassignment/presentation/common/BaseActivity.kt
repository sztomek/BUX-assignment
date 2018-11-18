package hu.sztomek.buxassignment.presentation.common

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import hu.sztomek.buxassignment.presentation.model.PersistableModel
import timber.log.Timber
import javax.inject.Inject

abstract class BaseActivity<out M : PersistableModel> : FragmentActivity() {

    private companion object {
        private const val KEY_STATE = "persistable_state"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(getViewModelClass())
        viewModel.takeInitialState(UiState.IdleState(getInitialState(savedInstanceState)))
        viewModel.stateStream.observe(
                this@BaseActivity,
                Observer {
                    Timber.d("new state: [$it]")
                    render(it)
                }
        )

        initUi()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        persistState(outState)
    }

    private fun getInitialState(savedInstanceState: Bundle?): M {
        return savedInstanceState?.getParcelable(KEY_STATE) ?: getDefaultInitialState()
    }

    private fun persistState(outBundle: Bundle?) {
        viewModel.stateStream.value?.data?.let {
            outBundle?.putParcelable(KEY_STATE, it)
        }
    }

    protected abstract fun initUi()
    protected abstract fun getDefaultInitialState(): M
    protected abstract fun getViewModelClass(): Class<out BaseViewModel>
    protected abstract fun render(it: UiState?)
}
