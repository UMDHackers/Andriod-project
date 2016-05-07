package com.finalproject.cmsc436.timelap;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Created by parth on 5/7/2016.
 */

public class TestCreateUser extends ActivityInstrumentationTestCase2<MainActivity> {
        private Solo solo;

        public TestCreateUser() {
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
            solo.clickOnView(solo.getView(R.id.sign_up));
            solo.enterText((EditText) solo.getView(com.finalproject.cmsc436.timelap.R.id.enter_username), "jack");
            solo.enterText((EditText) solo.getView(com.finalproject.cmsc436.timelap.R.id.enter_email), "jack@gmail.com");
            solo.enterText((EditText) solo.getView(R.id.enter_password), "password");
            solo.enterText((EditText) solo.getView(R.id.confirm_password), "password");
            solo.clickOnView(solo.getView(com.finalproject.cmsc436.timelap.R.id.create_account));
            String successMessage = "Successfully created user account";
            assertTrue(successMessage + " is not shown",
                    solo.waitForText(successMessage, 1, longDelay));
        }

}


