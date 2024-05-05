package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.FavVerse
import io.realm.kotlin.ext.query



class FavBooksFragment : Fragment() {
    private lateinit var viewModel: BibleBooksViewModel
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_books, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = BibleBooksViewModel()

        email = DatabaseConnector.email

        val favoriteVerses = DatabaseConnector.db.query<FavVerse>("owner == $0",email).find()
        for (favVerse in favoriteVerses) {
            createFavBookLayout(view,favVerse.chapter,favVerse.verse,favVerse.totalVerses)
        }
        DatabaseConnector.db.close()
    }
    private fun onFavClick(name:String, chapters:Int,actualChapter:Int){
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putInt("chapters", chapters)
        bundle.putInt("actualChapter", actualChapter)
        val fragment = LectureFragment()
        fragment.arguments = bundle
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun createFavBookLayout(view: View, name: String, actualChapter: Int, chapters: Int) {
        val parent = view.findViewById<LinearLayout>(R.id.LibrosLayout)
        val linearLayout = LinearLayout(requireContext())
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setOnClickListener {
            onFavClick(name, chapters, actualChapter)
        }

        val textView1 = TextView(requireContext())
        textView1.layoutParams = LinearLayout.LayoutParams(500, 100)
        textView1.text = "  $name"
        textView1.gravity = Gravity.CENTER
        textView1.textSize = 22f


        val textView3 = TextView(requireContext())
        textView3.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView3.text = actualChapter.toString()
        textView3.gravity = Gravity.CENTER
        textView3.textSize = 20f

        linearLayout.addView(textView1)
        linearLayout.addView(textView3)

        parent.addView(linearLayout)
    }
}