package com.iglesiabfr.iglesiabfrnaranjo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class BibleBooks : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bible_books)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        coroutineScope.launch{
            var link = "https://bible-api.deno.dev/api/books"
            var response = getRequest(link)
            //prueba
            val jsonArrayObj = JSONArray(response.toString())
            val books = mutableListOf<Book>()
            for (i in 0 until jsonArrayObj.length()) {
                val jsonObject = jsonArrayObj[i] as JSONObject

                val names = jsonObject.getJSONArray("names")
                val name = if (names.length() > 0) names[0] else ""

                val abrev = jsonObject.getString("abrev")
                val chapters = jsonObject.getInt("chapters")
                val testament = jsonObject.getString("testament")

                val book = Book(name, abrev, chapters, testament)
                books.add(book)
            }

            println(books.get(0).name)

        }
    }

}

data class Book(
    val name: Any,
    val abbreviation: String,
    val chapters: Int,
    val testament: String
)