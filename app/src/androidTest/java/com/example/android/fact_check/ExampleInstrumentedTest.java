package com.example.android.fact_check;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ExampleInstrumentedTest {

    private IdlingResource mIdlingResource;
//    @Rule
//    public ActivityScenarioRule<MainActivity> activityRule =
//            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
            @Override
            public void perform(MainActivity activity) {
                mIdlingResource = activity.getIdlingResource();
                IdlingRegistry.getInstance().register(mIdlingResource);
                System.out.println("midlingresource" + mIdlingResource.isIdleNow());
            }
        });
    }


    @Test
    public void testSearchBox() {
        onView(withId(R.id.search_text)).perform(typeText("Modi"), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
//        onView(withId(R.id.progressbar)).check
//                (matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


//        onView(withId(R.id.recycler_view)).
//                perform(RecyclerViewActions.scrollToPosition(0));
    /*    try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        onView(withId(R.id.searched_text)).check(matches(withText("Modi")));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
//    @Test
//    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        assertEquals("com.example.android.fact_check", appContext.getPackageName());
//    }

    /* TODO
     *   Tests for
     *   Nested RecyclerView
     *   RecyclerView images
     *   RecyclerView size
     *
     *
     * */

}
