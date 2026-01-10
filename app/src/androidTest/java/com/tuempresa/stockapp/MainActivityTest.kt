package com.tuempresa.stockapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Test
    fun mainActivity_launches_and_showsNavHost() {
        ActivityScenario.launch(MainActivity::class.java).use { _ ->
            onView(withId(R.id.nav_host_fragment)).check(matches(isDisplayed()))
        }
    }
}
