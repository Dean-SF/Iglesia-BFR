package com.iglesiabfr.iglesiabfrnaranjo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DailyVers : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mainHandler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_vers)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(R.layout.activity_daily_vers)
        mainHandler = Handler(Looper.getMainLooper())
        val DailyVersText = findViewById<TextView>(R.id.DailyVersText)
        val DailyVersVerse = findViewById<TextView>(R.id.DailyVerseVers)
        coroutineScope.launch {
            try{
                val url = URL("https://dailyverses.net/get/verse.js?language=nvi")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var inputLine: String?
                    val response = StringBuffer()

                    while (reader.readLine().also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    reader.close()

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
                        DailyVersText.text = bibleText
                        DailyVersVerse.text = bibleVerse
                    }

                } else {
                    runOnUiThread {
                        DailyVersText.text = getString(R.string.ErrorCargaMsg)
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