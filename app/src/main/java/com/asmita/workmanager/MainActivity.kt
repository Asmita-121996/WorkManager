package com.asmita.workmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.asmita.workmanager.WaterTrackingService.Companion.EXTRA_INTAKE_AMOUNT_MILLILITERS

class MainActivity : AppCompatActivity() {

    private val waterButton: View by lazy {
        findViewById(R.id.main_water_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launchTrackingService()

        waterButton.setOnClickListener {

            val intent = Intent(this,WorkManagerSchedule::class.java)
            startActivity(intent)
         //   launchTrackingService(250f)
        }
    }

    private fun launchTrackingService(intakeAmount: Float = 0f) {
        val serviceIntent =
            Intent(this, WaterTrackingService::class.java).apply {
                putExtra(EXTRA_INTAKE_AMOUNT_MILLILITERS, intakeAmount)
            }
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}