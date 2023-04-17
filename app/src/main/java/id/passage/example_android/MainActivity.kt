package id.passage.example_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import id.passage.android.PassageAuth
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testRegister()
    }

    private fun testRegister() {
        runBlocking {
            val response = PassageAuth.register("ricky.padilla+1@passage.id")
            Log.d(TAG, response ?: "No response")
        }
    }
}
