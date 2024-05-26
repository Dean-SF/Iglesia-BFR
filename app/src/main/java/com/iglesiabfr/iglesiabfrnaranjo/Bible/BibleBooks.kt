package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.Requests.getRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
                val name = names[0]

                val abrev = jsonObject.getString("abrev")
                val chapters = jsonObject.getInt("chapters")
                val testament = jsonObject.getString("testament")

                val book = Book(name, abrev, chapters, testament,0)

                books.add(book)
            }
            for (book in books){
                createBookLayout(book.name,book.abbreviation,book.chapters)
            }

            println(books.get(0).name)

        }
    }

    private fun createBookLayout(name: Any, abrev:String, chapters:Int){
        val typeFace = ResourcesCompat.getFont(this,R.font.comfortaa_light)
        runOnUiThread {
            val parent = findViewById<LinearLayout>(R.id.LibrosLayout)
            val linearLayout = LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            //linearLayout.setBackgroundColor(Color.parseColor("#5F5D5D"))

            val button = Button(this)
            button.layoutParams =
                LinearLayout.LayoutParams(47, LinearLayout.LayoutParams.WRAP_CONTENT)
            button.background = ContextCompat.getDrawable(this, R.drawable.play_btn)

            val textView1 = TextView(this)
            textView1.typeface = typeFace
            textView1.layoutParams =
                LinearLayout.LayoutParams(500, 100)
            textView1.text = "  $name"
            textView1.gravity = Gravity.LEFT
            textView1.textSize = 22f

            val textView2 = TextView(this)
            textView2.typeface = typeFace
            textView2.layoutParams =
                LinearLayout.LayoutParams(5,100, 1f)
            textView2.text = abrev
            textView2.gravity = Gravity.CENTER
            textView2.textSize = 20f

            val textView3 = TextView(this)
            textView3.typeface = typeFace
            textView3.layoutParams =
                LinearLayout.LayoutParams(5, 100, 1f)
            textView3.text = chapters.toString()
            textView3.gravity = Gravity.CENTER
            textView3.textSize = 20f


            linearLayout.addView(textView1)
            linearLayout.addView(textView2)
            linearLayout.addView(textView3)
//            linearLayout.addView(button)

            parent.addView(linearLayout)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}

data class Book(
    val name: Any,
    val abbreviation: String,
    val chapters: Int,
    val testament: String,
    val actual:Int
)