package com.iglesiabfr.iglesiabfrnaranjo.forums.pastor

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.PublicacionForoPastor
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class PastorForumFragment : Fragment() {
    private var admin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
        admin = DatabaseConnector.getIsAdmin()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addPublicacion = view.findViewById<Button>(R.id.addPublicacionForoPastorBtn)
        addPublicacion.setOnClickListener(){
            addPublication()
        }
        if (!admin){
            addPublicacion.visibility = View.INVISIBLE;
        }
        loadPublications(view)

    }

    private fun loadPublications(view: View) {
        val forumPublications = DatabaseConnector.db.query<PublicacionForoPastor>().sort("date", Sort.DESCENDING).find()
        val parent = view.findViewById<LinearLayout>(R.id.pastorForumLayout)
        for (publication in forumPublications){
            val layout = createPublicationLayOut(view,publication)
            parent.addView(layout)
        }
    }

    private fun createPublicationLayOut(view: View,publication: PublicacionForoPastor): LinearLayout {

        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(16, 16, 16, 16)

        // Título
        val titleTextView = TextView(requireContext())
        titleTextView.text = publication.title
        titleTextView.textSize = 22.0F
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
        }
        linearLayout.addView(titleTextView)

        // Contenido
        val contentTextView = TextView(requireContext())
        contentTextView.text = publication.content
        titleTextView.textSize = 20.0F
        contentTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
            topMargin = 8
        }
        linearLayout.addView(contentTextView)

        //Obtener la fecha y hora
        val realmInstant = publication.date
        val instant = Instant.ofEpochSecond(realmInstant.epochSeconds, realmInstant.nanosecondsOfSecond.toLong())
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dia = date.dayOfMonth
        val mes = date.monthValue
        val ano = date.year
        val fecha = "$dia/$mes/$ano"
        val dateTextView = TextView(requireContext())
        dateTextView.text = fecha
        dateTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.START
            topMargin = 8
        }
        linearLayout.addView(dateTextView)


        linearLayout.setOnLongClickListener {
            if(admin){
                // Mostrar un diálogo de confirmación
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Eliminar publicación")
                builder.setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
                builder.setPositiveButton("Eliminar") { _, _ ->
                    // Eliminar la publicación
                    deletePublicationFromMongo(publication._id)
                    // Eliminar el layout
                    (linearLayout.parent as ViewGroup).removeView(linearLayout)
                }
                builder.setNegativeButton("Cancelar", null)
                builder.show()
            }
            true
        }
        return linearLayout
    }

    private fun deletePublicationFromMongo(_id: ObjectId) {
        try {
            DatabaseConnector.db.writeBlocking {
                val postQuery = query<PublicacionForoPastor>("_id == $0", _id).find()
                if (postQuery.isNotEmpty()) {
                    val fav = postQuery[0]
                    delete(fav)
                    lifecycleScope.launch {
                        Toast.makeText(context, "Se ha borrado la publicació", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    lifecycleScope.launch {
                        Toast.makeText(context, "No se ha podido la publicació", Toast.LENGTH_SHORT).show()
                    }
                    println("No borrado")
                }
            }
        } catch (e: Exception) {
            lifecycleScope.launch {
                Toast.makeText(context, "Error al borrar la publicación", Toast.LENGTH_SHORT).show()
            }
            println("Error: $e")
        }
    }

    private fun addPublication() {
        val fragment = PastorForumPublicationFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pastor_forum, container, false)
    }


}