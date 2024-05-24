package com.iglesiabfr.iglesiabfrnaranjo.forums.petition

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Petition
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class PetitionsForumFragment : Fragment() {
    private var admin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        admin = DatabaseConnector.getIsAdmin()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_petitions_forum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddPetition = view.findViewById<Button>(R.id.addPetitionBtn)
        btnAddPetition.setOnClickListener(){
            addPetition()
        }
        loadPublications(view)
    }

    private fun addPetition() {
        val fragment = PetitionForumPublicationFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun loadPublications(view: View) {
        val forumPublications = DatabaseConnector.db.query<Petition>().sort("date", Sort.DESCENDING).find()
        val parent = view.findViewById<LinearLayout>(R.id.petitionForumLayout)
        for (publication in forumPublications){
            val layout = createPublicationLayOut(view,publication)
            parent.addView(layout)
        }
    }

    private fun createPublicationLayOut(view: View, publication: Petition): LinearLayout {
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setPadding(16, 16, 16, 16)

        // Imagen
        val imageView = ImageView(requireContext())
        imageView.setImageResource(R.drawable.profile) // Reemplaza con el nombre de tu drawable
        imageView.layoutParams = LinearLayout.LayoutParams(
            100, // Ancho de la imagen en píxeles
            100 // Alto de la imagen en píxeles
        ).apply {
            marginEnd = 16 // Espacio entre la imagen y el contenido
            gravity = Gravity.CENTER_VERTICAL // Centrar verticalmente la imagen
        }
        linearLayout.addView(imageView)

        // Contenido
        val contentLayout = LinearLayout(requireContext())
        contentLayout.orientation = LinearLayout.VERTICAL

        // Título
        val titleTextView = TextView(requireContext())
        titleTextView.text = publication.type
        titleTextView.textSize = 22.0F
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
        }
        contentLayout.addView(titleTextView)

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
        contentLayout.addView(contentTextView)

        // Fecha y hora
        val dateTextView = TextView(requireContext())
        val realmInstant = publication.date
        val instant = Instant.ofEpochSecond(realmInstant.epochSeconds, realmInstant.nanosecondsOfSecond.toLong())
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dia = date.dayOfMonth
        val mes = date.monthValue
        val ano = date.year
        val fecha = "$dia/$mes/$ano"
        dateTextView.text = fecha
        dateTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.START
            topMargin = 8
        }
        contentLayout.addView(dateTextView)

        linearLayout.addView(contentLayout)

        linearLayout.setOnLongClickListener {
            if (admin) {
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
                val postQuery = query<Petition>("_id == $0", _id).find()
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
}