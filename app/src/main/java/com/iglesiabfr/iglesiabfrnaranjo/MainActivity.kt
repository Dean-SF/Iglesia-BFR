package com.iglesiabfr.iglesiabfrnaranjo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iglesiabfr.iglesiabfrnaranjo.ui.theme.IglesiaBFRNaranjoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val i =  Intent(this,Testingdb::class.java)
//        val i =  Intent(this,DailyVers::class.java)
        val i =  Intent(this,BibleBooks::class.java)
        startActivity(i)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IglesiaBFRNaranjoTheme {
        Greeting("Android")
    }
}