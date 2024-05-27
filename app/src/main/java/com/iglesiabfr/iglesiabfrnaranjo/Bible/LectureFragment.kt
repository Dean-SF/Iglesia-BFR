package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.iglesiabfr.iglesiabfrnaranjo.CustomSpinnerAdapter
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.FavVerse
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
    private var email = ""
    private lateinit var chapterSpinner: Spinner
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            chapters = it.getInt("chapters")
            actualChapter = it.getInt("actualChapter",1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email = DatabaseConnector.email
        loadingDialog.startLoading()
        fetchVerses()
        val nextBtn = view.findViewById<Button>(R.id.nextBtn)
        val favBtn = view.findViewById<Button>(R.id.favBtn)
        val prevBtn = view.findViewById<Button>(R.id.prevBtn)
        chapterSpinner = view.findViewById(R.id.chapterSpinner)
        val chapterOptions = ArrayList<String>()
        for (i in 1..chapters!!) {
            chapterOptions.add("Capítulo $i")
        }
        chapterSpinner.adapter = CustomSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, chapterOptions)

        // Posicionar el spinner en la posición de actualChapter
        chapterSpinner.setSelection(actualChapter - 1)
        
        chapterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualChapter = position + 1
                loadingDialog.startLoading()
                fetchVerses()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        nextBtn.setOnClickListener{
            nextVerse()
        }
        favBtn.setOnClickListener {
            addFav()
        }
        prevBtn.setOnClickListener{
            prevVerse()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lecture, container, false)
        loadingDialog = LoadingDialog(view.context)
        return view
    }

    private fun fetchVerses() {
        val name = name
        val chapter = actualChapter
        val verseTitleTextView = view?.findViewById<TextView>(R.id.verseTitleTextView)
        verseTitleTextView?.text = "$name"
        val request = Request.Builder()
            .url("https://bible-api.deno.dev/api/read/rv1960/$name/$chapter/")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle error
                loadingDialog.stopLoading()
            }

            override fun onResponse(call: Call, response: Response) {
                activity?.runOnUiThread {
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
                        loadingDialog.stopLoading()
                    }
                }
            }
        })
    }
    private fun nextVerse() {
        val nextPosition = chapterSpinner.selectedItemPosition + 1
        if (nextPosition < chapterSpinner.adapter.count) {
            chapterSpinner.setSelection(nextPosition)
            loadingDialog.startLoading()
            fetchVerses()
        }
    }

    private fun prevVerse() {
        val prevPosition = chapterSpinner.selectedItemPosition - 1
        if (prevPosition >= 0) {
            chapterSpinner.setSelection(prevPosition)
            loadingDialog.startLoading()
            fetchVerses()
        }
    }
    private fun addFav() {
        try {
            val actualFav = FavVerse().apply {
                chapter = name.toString()
                totalVerses = chapters!!
                verse = actualChapter
                owner = email
            }
            DatabaseConnector.db.writeBlocking {
                copyToRealm(actualFav)
            }
            // Mostrar un Toast para indicar que se guardó el favorito
            requireContext().toast("Verso guardado como favorito")
        } catch (e: Exception) {
            println("Error al guardar favorito $e")
            // Mostrar un Toast para indicar que hubo un error al guardar el favorito
            requireContext().toast("Error al guardar el favorito")
        }
    }

    private fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
}
