package com.DTU.concussionclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TestEndscreenActivity : AppCompatActivity() {

    private val concussionApplication get() = application as ConcussionApplication
    private val isScreening get() = concussionApplication.getIsScreening
    private val isBaseline get() = !isScreening
    private val baselineData get() = concussionApplication.getBaselineTempData
    private val isFirstAttempt get() = baselineData.firstAttempt.isNaN()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_endscreen)

        if (isBaseline)
            handleBaselineData()

        addTestTypeFragment()
    }

    private fun handleBaselineData() {
        if (isFirstAttempt) {
            baselineData.firstAttempt = concussionApplication.calculateFinalScore()
        }
        else {
            baselineData.secondAttempt = concussionApplication.calculateFinalScore()
        }
    }

    private fun addTestTypeFragment() {
        if(isScreening) addScreeningFragment() else addBaselineFragment()
    }

    private fun addScreeningFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, EndscreenFragment_Screening()).commitAllowingStateLoss()
    }

    private fun addBaselineFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, EndscreenFragment_Baseline()).commitAllowingStateLoss()
    }
}