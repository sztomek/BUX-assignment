package hu.sztomek.buxassignment.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.interactor.GetSelectableProductsInteractor
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.domain.model.SelectableProduct
import hu.sztomek.buxassignment.domain.resource.ResourceHelper
import hu.sztomek.buxassignment.domain.scheduler.WorkSchedulers
import hu.sztomek.buxassignment.presentation.common.UiError
import hu.sztomek.buxassignment.presentation.common.UiState
import hu.sztomek.buxassignment.presentation.model.ProductSelectModel
import hu.sztomek.buxassignment.presentation.screen.productselect.ProductSelectViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


@RunWith(JUnit4::class)
class ProductSelectViewModelTest {

    private companion object {
        private val immediate: Scheduler = object : Scheduler() {

            override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Scheduler.Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }

        private const val DUMMY_STRING = "dummy"
        private val DUMMY_PRODUCTS = listOf<ISelectableProduct>(
            SelectableProduct("dummy1", "1"),
            SelectableProduct("dummy2", "2"),
            SelectableProduct("dummy3", "3")
        )
        private val DEFAULT_STATE = ProductSelectModel(null)
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dataRepository: DataRepository
    @Mock
    private lateinit var resourceHelper: ResourceHelper
    @Mock
    private lateinit var schedulers: WorkSchedulers
    @Mock
    private lateinit var observer: Observer<UiState>

    private val getSelectableProductsInteractor by lazy {
        GetSelectableProductsInteractor(dataRepository)
    }

    private val viewModel by lazy {
        ProductSelectViewModel(getSelectableProductsInteractor, resourceHelper, schedulers)
    }

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        `when`(resourceHelper.getString(anyInt())).thenReturn(DUMMY_STRING)
        `when`(schedulers.io()).thenReturn(immediate)
        `when`(schedulers.computation()).thenReturn(immediate)
        `when`(schedulers.ui()).thenReturn(immediate)
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.just(DUMMY_PRODUCTS))

        viewModel.takeInitialState(UiState.IdleState(DEFAULT_STATE))
    }

    @Test
    fun `test GetSelectableProducts action triggers repository`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.just(DUMMY_PRODUCTS))

        viewModel.sendAction(Action.GetSelectableProducts)

        verify(dataRepository).getSelectableProducts()
    }

    @Test
    fun `test GetSelectableProducts action triggers proper ui state changes on happy case`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.just(DUMMY_PRODUCTS))
        viewModel.stateStream.observeForever(observer)

        val argumentCaptor = ArgumentCaptor.forClass(UiState::class.java)
        val expectedDefaultState = UiState.IdleState(DEFAULT_STATE)
        val expectedLoadingState = UiState.LoadingState(DUMMY_STRING, DEFAULT_STATE)
        val expectedFinishedState = UiState.IdleState(DEFAULT_STATE.copy(selectableProducts = DUMMY_PRODUCTS))
        argumentCaptor.run {
            verify(observer, times(3)).onChanged(capture())
            val (initialState, loadingState, finishedState) = allValues
            assertEquals(initialState, expectedDefaultState)
            assertEquals(loadingState, expectedLoadingState)
            assertEquals(finishedState, expectedFinishedState)
        }

        viewModel.sendAction(Action.GetSelectableProducts)
    }

    @Test
    fun `test GetSelectableProducts action triggers proper ui state changes on error case`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.error(RuntimeException("fail")))
        viewModel.stateStream.observeForever(observer)

        val argumentCaptor = ArgumentCaptor.forClass(UiState::class.java)
        val expectedDefaultState = UiState.IdleState(DEFAULT_STATE)
        val expectedLoadingState = UiState.LoadingState(DUMMY_STRING, DEFAULT_STATE)
        val expectedFinishedState = UiState.ErrorState(UiError.GeneralUiError(DUMMY_STRING), DEFAULT_STATE)
        argumentCaptor.run {
            verify(observer, times(3)).onChanged(capture())
            val (initialState, loadingState, finishedState) = allValues
            assertEquals(initialState, expectedDefaultState)
            assertEquals(loadingState, expectedLoadingState)
            assertEquals(finishedState, expectedFinishedState)
        }

        viewModel.sendAction(Action.GetSelectableProducts)
    }

}