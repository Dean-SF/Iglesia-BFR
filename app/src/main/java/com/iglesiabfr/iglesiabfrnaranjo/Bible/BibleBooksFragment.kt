package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.Requests.getRequest
import com.iglesiabfr.iglesiabfrnaranjo.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class BibleBooksFragment : Fragment() {

    private lateinit var viewModel: BibleBooksViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bible_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = BibleBooksViewModel()
        val email = sharedViewModel.getEmail()
        println("a\na\na\na\na\na\na\na\na\na\na")
        println("Email recivido: $email")
        println("b\nb\nb\nb\nb\nb\nb\nb\nb\nb\nb")
        viewModel.getBooks().observe(viewLifecycleOwner) { books ->
            for (book in books) {
                createBookLayout(view, book.name, book.abbreviation, book.chapters)
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun createBookLayout(view: View, name: Any, abrev: String, chapters: Int) {
        val parent = view.findViewById<LinearLayout>(R.id.LibrosLayout)
        val linearLayout = LinearLayout(requireContext())
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setOnClickListener {
            onBookClick(name.toString(), chapters)
        }

        val textView1 = TextView(requireContext())
        textView1.layoutParams = LinearLayout.LayoutParams(500, 100)
        textView1.text = "  $name"
        textView1.gravity = Gravity.LEFT
        textView1.textSize = 22f

        val textView2 = TextView(requireContext())
        textView2.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView2.text = abrev
        textView2.gravity = Gravity.CENTER
        textView2.textSize = 20f

        val textView3 = TextView(requireContext())
        textView3.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView3.text = chapters.toString()
        textView3.gravity = Gravity.CENTER
        textView3.textSize = 20f

        linearLayout.addView(textView1)
        linearLayout.addView(textView2)
        linearLayout.addView(textView3)

        parent.addView(linearLayout)
    }
    private fun onBookClick(name:String, chapters: Int){
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("chapters", chapters)
        val fragment = LectureFragment()
        fragment.arguments = bundle

        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}


class BibleBooksViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val _books = MutableLiveData<List<Book>>()

    fun getBooks(): LiveData<List<Book>> {
        coroutineScope.launch {
            val link = "https://bible-api.deno.dev/api/books"
            val response = getRequest(link)
            val jsonArrayObj = JSONArray(response.toString())
            val books = mutableListOf<Book>()
            for (i in 0 until jsonArrayObj.length()) {
                val jsonObject = jsonArrayObj[i] as JSONObject

                val names = jsonObject.getJSONArray("names")
                val name = names[0]

                val abrev = jsonObject.getString("abrev")
                val chapters = jsonObject.getInt("chapters")
                val testament = jsonObject.getString("testament")

                val book = Book(name, abrev, chapters, testament)
                books.add(book)
            }
            _books.postValue(books)
        }
        return _books
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}