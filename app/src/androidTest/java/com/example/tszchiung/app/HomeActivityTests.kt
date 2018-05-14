package com.example.tszchiung.app

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTests {
    @Rule
    val activityRule: ActivityTestRule<HomeActivity> = ActivityTestRule<HomeActivity>(HomeActivity::class.java)


}