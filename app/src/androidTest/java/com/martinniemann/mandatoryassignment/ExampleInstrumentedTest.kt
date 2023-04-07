package com.martinniemann.mandatoryassignment

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.cdimascio.dotenv.dotenv

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.martinniemann.mandatoryassignment", appContext.packageName)

        Espresso.onView(ViewMatchers.withText("Login and Register"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.email))
            .perform(ViewActions.typeText(dotenv["USER_EMAIL"]))
        Espresso.onView(withId(R.id.password))
            .perform(ViewActions.typeText(dotenv["USER_PASSWORD"]))
            .perform(ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.loginButton))
            .perform(ViewActions.click())
        pause(2000)
        Espresso.onView(ViewMatchers.withText("Marketplace"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun pause(millis: Long) {
        try {
            Thread.sleep(millis)
            // https://www.repeato.app/android-espresso-why-to-avoid-thread-sleep/
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}