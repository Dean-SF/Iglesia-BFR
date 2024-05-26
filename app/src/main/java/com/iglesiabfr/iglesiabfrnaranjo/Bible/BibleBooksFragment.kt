package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.SharedViewModel
import com.iglesiabfr.iglesiabfrnaranjo.Verses.VersFragment
import com.iglesiabfr.iglesiabfrnaranjo.admin.emotions.SeeEmotions
import com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions.SuggestionsMailbox
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.emotions.SendEmotion
import com.iglesiabfr.iglesiabfrnaranjo.homepage.MyProfile
import com.iglesiabfr.iglesiabfrnaranjo.suggestions.SendSuggestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class BibleBooksFragment(val fragManager: FragmentManager) : Fragment() {

    private lateinit var viewModel: BibleBooksViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var email = ""
    private var popupMenu: PopupMenu? = null
    private var isSubMenuShowing: Boolean = false
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bible_books, container, false)
        loadingDialog = LoadingDialog(view.context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favBtn = view.findViewById<Button>(R.id.favBtnInBibleBooks)
        val dailyVersBtn = view.findViewById<Button>(R.id.dailyVersBtn)
        viewModel = BibleBooksViewModel()
        email = sharedViewModel.getEmail().toString()

        val profBut = view.findViewById<ImageView>(R.id.profBut)
        profBut.setOnClickListener {
            if (popupMenu == null || !isSubMenuShowing) {
                showSubMenu(profBut)
            }
        }
        loadingDialog.startLoading()
        viewModel.getBooks().observe(viewLifecycleOwner) { books ->
            for (book in books) {
                createBookLayout(view, book.name, book.abbreviation, book.chapters)
            }
            loadingDialog.stopLoading()
        }
        dailyVersBtn.setOnClickListener(){
            seeDaily()
        }
        favBtn.setOnClickListener(){
            openFav()
        }
    }

    private fun seeDaily() {
        val fragment = VersFragment()
        fragManager.beginTransaction()
            .replace(R.id.framelayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSubMenu(view: View) {
        popupMenu = PopupMenu(requireContext(), view)
        popupMenu!!.menuInflater.inflate(R.menu.submenu_profile, popupMenu!!.menu)
        popupMenu!!.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(activity, MyProfile::class.java))
                    true
                }
                R.id.menu_emotion_registration -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SeeEmotions::class.java))
                    } else {
                        startActivity(Intent(activity, SendEmotion::class.java))
                    }
                    true
                }
                R.id.menu_suggestion_box -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SuggestionsMailbox::class.java))
                    } else {
                        startActivity(Intent(activity, SendSuggestion::class.java))
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu!!.setOnDismissListener {
            isSubMenuShowing = false
        }
        popupMenu!!.show()
        isSubMenuShowing = true
    }

    private fun openFav() {
        val fragment = FavBooksFragment()

        fragManager.beginTransaction()
            .replace(R.id.framelayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    @SuppressLint("RtlHardcoded")
    private fun createBookLayout(view: View, name: Any, abrev: String, chapters: Int) {
        val parent = view.findViewById<LinearLayout>(R.id.LibrosLayout)
        val linearLayout = LinearLayout(requireContext())
        val typeFace = ResourcesCompat.getFont(this.requireContext(),R.font.comfortaa_light)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setOnClickListener {
            onBookClick(name.toString(), chapters)
        }

        val textView1 = TextView(requireContext())
        textView1.typeface = typeFace
        textView1.ellipsize = TextUtils.TruncateAt.END
        textView1.maxLines = 1
        textView1.layoutParams = LinearLayout.LayoutParams(500, 100)
        textView1.text = "  $name"
        textView1.gravity = Gravity.LEFT
        textView1.textSize = 19f

        val textView2 = TextView(requireContext())
        textView2.typeface = typeFace
        textView2.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView2.text = abrev
        textView2.gravity = Gravity.CENTER
        textView2.textSize = 17f

        val textView3 = TextView(requireContext())
        textView3.typeface = typeFace
        textView3.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView3.text = chapters.toString()
        textView3.gravity = Gravity.CENTER
        textView3.textSize = 17f

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

        fragManager.beginTransaction()
            .replace(R.id.framelayout, fragment)
            .addToBackStack(null)
            .commit()
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

                val book = Book(name, abrev, chapters, testament,0)
                books.add(book)
            }
            _books.postValue(books)
        }
        return _books
    }

    private fun getRequest(link:String): StringBuffer? {
        val url = URL(link)
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
            return response
        } else {
            return null
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}