package com.example.ka.mycoroutinesjobapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 //ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{
            if(!::job.isInitialized){
                initJob()
            }
            progressBar.startJobOrCancel(job)
        }

    }

    private fun ProgressBar.startJobOrCancel(job: Job){
        if(this.progress > 0){
            //job already start
            print("$job is already active. Cancelling..." )
            resetJob()
        }else{
            button.text = "cancel job #1"
            CoroutineScope(IO + job ).launch {
                println("Coroutine $this is activated with this job $job")

                for (i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME/ PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateTextViewMethod( "Job is completed" )
            }
        }
    }

    private fun updateTextViewMethod(textString: String){
        GlobalScope.launch(Main) {
            textView.text = textString
        }
    }

    private fun resetJob() {
       if(job.isActive || job.isCompleted){
           //pass exception to handle inside the initJob function
           job.cancel(CancellationException("Resetting Job"))
       }
        initJob()
    }

    private fun initJob(){
        button.text = "Start Job #1"
        updateTextViewMethod("")
        job = Job()

        job.invokeOnCompletion {
            //if it not null do this
            it?.message.let {
                var msg = it

                //String? mean the message can be nul
                if(msg.isNullOrBlank()){
                    msg = "Unknown"
                }

                println("$job was cancelled. Reason : $msg" )
                showToast(msg)
            }
        }

        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }


    private fun showToast(text: String){
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }

    }

}