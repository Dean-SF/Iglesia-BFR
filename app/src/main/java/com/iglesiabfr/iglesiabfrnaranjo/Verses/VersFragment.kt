package com.iglesiabfr.iglesiabfrnaranjo.Verses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.Requests.getRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


/**
 * A simple [Fragment] subclass.
 * Use the [VersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VersFragment : Fragment() {
    private lateinit var viewModel: VerseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(VerseViewModel::class.java)

        val dailyVerseText = view.findViewById<TextView>(R.id.DailyVersText)
        val dailyVerseVerse = view.findViewById<TextView>(R.id.DailyVerseVers)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val link = "https://dailyverses.net/get/verse.js?language=nvi"
                val response = getRequest(link)
                if (response != null) {
                    // obtaining data from response
                    val resp = response.toString()
//                    Log.d("D", "response")
//                    Log.d("D", resp)
//                    var html = resp.replace("\\u003c", "<").replace("\\u003e", ">").replace("\\u0022", "\"").replace("\\\"", "\"").replace("\\\\", "\\")
//                    html = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"))
                    val doc: Document? = cleanResponse(resp)
                    val bibleTextElement = doc?.select(".dailyVerses.bibleText")?.first()
                    val bibleVerseElement = doc?.select(".dailyVerses.bibleVerse")?.first()
                    val bibleVerse = bibleVerseElement?.text()
                    val bibleText = bibleTextElement?.text()

                    //prueba
                    val text = "Texto: $bibleText \n Versículo: $bibleVerse"
                    Log.d("D", text)

                    view.post {
                        dailyVerseText.text = bibleText
                        dailyVerseVerse.text = bibleVerse
                    }

                } else {
                    view.post {
                        dailyVerseText.text = getString(R.string.ErrorCargaMsg)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    private fun cleanResponse(response:String): Document? {
        //Cleaning response
        var html = response
            .replace("\\u003c", "<")
            .replace("\\u003e", ">")
            .replace("\\u0022", "\"")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
        html = html.substring(html.indexOf("'") + 1, html.lastIndexOf("'"))
        return Jsoup.parse(html)
    }



    class VerseViewModel : ViewModel() {
        // TODO: Add any necessary ViewModel logic here
    }
}