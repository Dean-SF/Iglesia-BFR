package com.iglesiabfr.iglesiabfrnaranjo.Verses

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.Requests.getRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DailyVers : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_vers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dailyVersText = findViewById<TextView>(R.id.DailyVersText)
        val dailyVersVerse = findViewById<TextView>(R.id.DailyVerseVers)
        coroutineScope.launch {
            try{
                val link = "https://dailyverses.net/get/verse.js?language=nvi"
                val response = getRequest(link)
                if (response != null) {
                    // obtaining data from response
                    val resp = response.toString()
                    Log.d("D", "response")
                    Log.d("D", resp)
                    var html = resp.replace("\\u003c", "<").replace("\\u003e", ">").replace("\\u0022", "\"").replace("\\\"", "\"").replace("\\\\", "\\")
                    html = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"))
                    val doc: Document = Jsoup.parse(html)
                    val bibleTextElement = doc.select(".dailyVerses.bibleText").first()
                    val bibleVerseElement = doc.select(".dailyVerses.bibleVerse").first()
                    val bibleVerse = bibleVerseElement.text()
                    val bibleText = bibleTextElement.text()

                    //prueba
                    val text = "Texto: $bibleText \n Versiculo: $bibleVerse"
                    Log.d("D", text)

                    runOnUiThread {
                        dailyVersText.text = bibleText
                        dailyVersVerse.text = bibleVerse
                    }

                } else {
                    runOnUiThread {
                        dailyVersText.text = getString(R.string.ErrorCargaMsg)
                    }
                }
            } catch (e:Exception){
                println(e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}