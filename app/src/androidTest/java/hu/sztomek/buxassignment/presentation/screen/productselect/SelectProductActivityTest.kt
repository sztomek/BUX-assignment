package hu.sztomek.buxassignment.presentation.screen.productselect

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import hu.sztomek.buxassignment.R
import hu.sztomek.buxassignment.domain.model.ISelectableProduct
import hu.sztomek.buxassignment.presentation.app.BUXAssignmentApplication
import hu.sztomek.buxassignment.presentation.screen.di.component.DaggerTestComponent
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectProductActivityTest {

    @get:Rule
    val mActivityRule = ActivityTestRule(ProductSelectActivity::class.java)

    @Before
    fun setUp() {
        DaggerTestComponent.builder()
            .application(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as BUXAssignmentApplication)
            .build()
            .inject(this)
    }

    @Test
    fun testSelectingGoldDisplaysGoldDetails() {
        onView(withId(R.id.productselect_spinner)).perform(click())
        onData(allOf(`is`(instanceOf(ISelectableProduct::class.java)))).atPosition(3).perform(click())
        onView(withId(R.id.productselect_button)).perform(click())

        Thread.sleep(2000L)

        onView(withId(R.id.productdetails_displayname)).check(matches(withText("Gold")))
    }

}