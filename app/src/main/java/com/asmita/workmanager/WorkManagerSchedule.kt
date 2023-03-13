package com.asmita.workmanager

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import java.util.concurrent.TimeUnit


class WorkManagerSchedule : AppCompatActivity() {

    companion object {
        val MESSAGE_STATUS = "WorkManagerSchedule"
    }

    private lateinit var btnEnqueueWork: Button
    private lateinit var btnCancelWork: Button
    private lateinit var tvWorkStatus: TextView
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var oneTimeWorkRequest: OneTimeWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.work_manager)

        btnEnqueueWork = findViewById<View>(R.id.buttonEnqueueWork) as Button
        btnCancelWork = findViewById<View>(R.id.buttonCancelWork) as Button
        tvWorkStatus = findViewById<View>(R.id.textViewWorkStatus) as TextView

        val mWorkManager = WorkManager.getInstance(this)
        val networkConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Comment by Asmita (Without Data) : For Calling it only once...
            // oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()

            // Comment by Asmita (With Data) : For Calling it only once...
            oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setConstraints(networkConstraints)
                .setInputData(
                    Data.Builder()
                        .putString(MESSAGE_STATUS, "Ashmita - OneTimeWorkRequest WorkManager")
                        .build()
                )
                .build()


            //Comment by Asmita : Schedule to repeat it after specific interval of time frequently....
            // periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES).build()

            // Comment by Asmita (With Data) : For Calling it only once...
            periodicWorkRequest =
                PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
                    .setConstraints(networkConstraints)
                    .setInputData(
                        Data.Builder()
                            .putString(MESSAGE_STATUS, "Naisha - PeriodicWorkRequest WorkManager")
                            .build()
                    )
                    .build()
        }

        btnEnqueueWork.setOnClickListener { mWorkManager.enqueue(periodicWorkRequest) }

        btnCancelWork.setOnClickListener { mWorkManager.cancelWorkById(periodicWorkRequest.id) }

        mWorkManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this) { workInfo ->
            if (workInfo != null) {
                val state = workInfo.state
                tvWorkStatus.append(
                    """
                            $state
                            
                            """.trimIndent()
                )
            }
        }
    }
}