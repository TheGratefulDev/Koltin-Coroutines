package com.example.ka.mycoroutinesapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.MainThread
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val RESULT_2 = "Result #2"
    private val RESULT_1 = "Result #1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{

            //three type of scope,
            //IO  (Input/output. ex: network or disk transactions)
            //Main  (UI Interactions)
            //Default (CPU intensive work)
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }

        }

    }

    private fun setNewText(input:String){
        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(input: String){
        withContext(Main){
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest(){
        val result1 = getResult1FromApi()
        print("Debug: $result1")
        setTextOnMainThread(result1)

        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)

    }

    //suspend keyword will mark this function can be called in the coroutine,
    // therefor execute in background thread

    //Coroutine is not thread
    //many coroutines can be execute in one thread
    private suspend fun getResult1FromApi() :String{
        logThread("getResult1FromApi")
        delay(1000) //this is a coroutine thing not the same as Thread.Sleep()
        return RESULT_1
    }


    private suspend fun getResult2FromApi() :String{
        logThread("getResult2FromApi")
        delay(1000)
        return RESULT_2
    }

    private fun logThread(methodName: String){
        println( "Debug :  $methodName : ${Thread.currentThread().name}")
    }

}