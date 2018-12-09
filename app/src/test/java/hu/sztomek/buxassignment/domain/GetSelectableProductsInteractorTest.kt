package hu.sztomek.buxassignment.domain

import hu.sztomek.buxassignment.domain.action.Action
import hu.sztomek.buxassignment.domain.data.DataRepository
import hu.sztomek.buxassignment.domain.error.DomainException
import hu.sztomek.buxassignment.domain.interactor.GetSelectableProductsInteractor
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class GetSelectableProductsInteractorTest {

    @Mock
    private lateinit var dataRepository: DataRepository

    private lateinit var interactor: GetSelectableProductsInteractor

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)

        interactor = GetSelectableProductsInteractor(dataRepository)
    }

    @Test
    fun `test execute relies on dataRepository`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.just(emptyList()))

        interactor.execute(Action.GetSelectableProducts).test()

        verify(dataRepository).getSelectableProducts()
    }

    @Test
    fun `test execute happy case`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.just(emptyList()))

        interactor.execute(Action.GetSelectableProducts).test()
            .assertValue { it.products.isEmpty() }
            .assertNoErrors()
            .assertTerminated()
    }

    @Test
    fun `test execute delegates signaled errors`() {
        `when`(dataRepository.getSelectableProducts()).thenReturn(Single.error(DomainException.GeneralDomainException()))

        interactor.execute(Action.GetSelectableProducts).test()
            .assertError { it is DomainException.GeneralDomainException }
            .assertTerminated()
    }
}