package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

private val client = OkHttpClient()
class LectureFragment : Fragment() {
    private var name: String? = null
    private var chapters: Int? = null
    private var actualChapter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            chapters = it.getInt("chapters")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchVerses()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lecture, container, false)
    }

    private fun fetchVerses() {
        val name = name
        val chapter = actualChapter
        val verseTitleTextView = view?.findViewById<TextView>(R.id.verseTitleTextView)
        verseTitleTextView?.text = "$name, Capítulo $actualChapter"
        val request = Request.Builder()
            .url("https://bible-api.deno.dev/api/read/rv1960/$name/$chapter/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    val verseText = view?.findViewById<TextView>(R.id.verseText)
                    val json = response.body?.string()
                    if (json != null) {
                        val jsonObject = JSONObject(json)
                        val versesArray = jsonObject.getJSONArray("vers")
                        var chapterText = ""
                        for (i in 0 until versesArray.length()) {
                            val verseObject = versesArray[i] as JSONObject
                            val verse = verseObject.getString("verse")
                            // Do something with the verse
                            chapterText += verse
                        }
                        // Clear the verseText view
                        verseText?.text = ""
                        // Set the chapterText to the verseText view
                        verseText?.append(chapterText)
                        println(chapterText)
                    }
                }
            }
        })
    }

    fun nextVerse(view: View) {
        actualChapter ++
        fetchVerses()
    }

}
