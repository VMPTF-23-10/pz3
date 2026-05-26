package com.nure.vmptf.pz3

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.openSumButton).setOnClickListener {
            openScreen(SumActivity::class.java)
        }
        findViewById<Button>(R.id.openCalendarButton).setOnClickListener {
            openScreen(CalendarActivity::class.java)
        }
        findViewById<Button>(R.id.openWheelButton).setOnClickListener {
            openScreen(WheelOfFortuneActivity::class.java)
        }
        findViewById<Button>(R.id.openRomanButton).setOnClickListener {
            openScreen(RomanConverterActivity::class.java)
        }
    }

    private fun openScreen(activityClass: Class<out Activity>) {
        startActivity(Intent(this, activityClass))
    }
}
