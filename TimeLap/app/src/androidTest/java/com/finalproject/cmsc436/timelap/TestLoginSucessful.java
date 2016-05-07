package com.finalproject.cmsc436.timelap;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

import java.lang.Override;

/**
 * Created by parth on 5/7/2016.
 */
public class TestLoginSucessful extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public TestLoginSucessful() {
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
        solo.enterText((EditText) solo.getView(com.finalproject.cmsc436.timelap.R.id.email), "parthdesai95@gmail.com");
        solo.enterText((EditText) solo.getView(com.finalproject.cmsc436.timelap.R.id.password), "parth");
        solo.clickOnView(solo.getView(com.finalproject.cmsc436.timelap.R.id.login));
        solo.waitForActivity(com.finalproject.cmsc436.timelap.MainActivity.class, delay);
        assertTrue("com.finalprojectonCreate: inmain not shown", solo.waitForLogMessage("com.finalprojectonCreate: inmain"));

    }

}


