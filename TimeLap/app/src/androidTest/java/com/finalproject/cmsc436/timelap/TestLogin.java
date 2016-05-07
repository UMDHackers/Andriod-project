package com.finalproject.cmsc436.timelap;

import android.test.ActivityInstrumentationTestCase2;

import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by parth on 5/7/2016.
 */
public class TestLogin extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public TestLogin() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
    public void testRun() {
        int delay = 2000;
        int longDelay = 5000;

        solo.waitForActivity(com.finalproject.cmsc436.timelap.MainActivity.class, delay);
        solo.sleep(delay);
        solo.clickOnView(solo.getView(com.finalproject.cmsc436.timelap.R.id.login));
        String failedLogin = "Your email or password is not correct.";
        assertTrue(failedLogin+ " is not shown",
                solo.waitForText(failedLogin, 1, longDelay));
    }

}
