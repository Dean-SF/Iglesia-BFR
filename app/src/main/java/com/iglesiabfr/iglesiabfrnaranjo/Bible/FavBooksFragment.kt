package com.iglesiabfr.iglesiabfrnaranjo.Bible

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.FavVerse
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch


class FavBooksFragment : Fragment() {
    private lateinit var viewModel: BibleBooksViewModel
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val typeFace = ResourcesCompat.getFont(this.requireContext(),R.font.comfortaa_light)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setOnClickListener {
            onFavClick(name, chapters, actualChapter)
        }
        linearLayout.setOnLongClickListener {
            // Mostrar un diálogo de confirmación
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Eliminar favorito")
            builder.setMessage("¿Estás seguro de que deseas eliminar este favorito?")
            builder.setPositiveButton("Eliminar") { _, _ ->
                // Eliminar el favorito
                deletefav(email, name, actualChapter)
                // Eliminar el layout
                (linearLayout.parent as ViewGroup).removeView(linearLayout)
            }
            builder.setNegativeButton("Cancelar", null)
            builder.show()
            true
        }

        val textView1 = TextView(requireContext())
        textView1.typeface = typeFace
        textView1.layoutParams = LinearLayout.LayoutParams(700, 100)
        textView1.text = "  $name"
        textView1.gravity = Gravity.CENTER
        textView1.textSize = 22f


        val textView3 = TextView(requireContext())
        textView3.typeface = typeFace
        textView3.layoutParams = LinearLayout.LayoutParams(5, 100, 1f)
        textView3.text = actualChapter.toString()
        textView3.gravity = Gravity.CENTER
        textView3.textSize = 20f

        linearLayout.addView(textView1)
        linearLayout.addView(textView3)

        parent.addView(linearLayout)
    }

    private fun deletefav(owner:String,name:String,verse:Int) {
        try {
            DatabaseConnector.db.writeBlocking {
                val favQuery = query<FavVerse>("owner == $0 AND chapter == $1 AND verse == $2", owner, name, verse).find()
                if (favQuery.isNotEmpty()) {
                    val fav = favQuery[0]
                    delete(fav)
                    lifecycleScope.launch {
                        Toast.makeText(context, "Se ha borrado el favorito", Toast.LENGTH_SHORT).show()
                    }
                    println("borrado")
                } else {
                    lifecycleScope.launch {
                        Toast.makeText(context, "No se ha podido borrar el favorito", Toast.LENGTH_SHORT).show()
                    }
                    println("No borrado")
                }
            }
        } catch (e: Exception) {
            lifecycleScope.launch {
                Toast.makeText(context, "Error al borrar el favorito", Toast.LENGTH_SHORT).show()
            }
            println("Error: $e")
        }
    }
}