package com.iglesiabfr.iglesiabfrnaranjo.forums.followup

import android.app.AlertDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.forums.pastor.PastorForumPublicationFragment
import com.iglesiabfr.iglesiabfrnaranjo.schema.Followup
import com.iglesiabfr.iglesiabfrnaranjo.schema.PublicacionForoPastor
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class followupForumFragment : Fragment() {
    private var admin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
        admin = DatabaseConnector.getIsAdmin()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val agregarPublicacion = view.findViewById<Button>(R.id.addPublicacionForoFollowupBtn)
        agregarPublicacion.setOnClickListener(){
            addPublication()
        }
        loadPublications(view)
    }

    private fun loadPublications(view: View) {
        val forumPublications = DatabaseConnector.db.query<Followup>().sort("date", Sort.DESCENDING).find()
        val parent = view.findViewById<LinearLayout>(R.id.folluwupForumLayout)
        parent.removeAllViews() // Eliminar todas las vistas anteriores

        for (publication in forumPublications) {
            val layout = createPublicationLayout(view, publication)
            parent.addView(layout)

            if (publication.answerContent.isNotEmpty()) {
                val responseLayout = createResponseLayout(publication)
                parent.addView(responseLayout)
            }
        }
    }



    private fun createPublicationLayout(view: View, publication: Followup): View? {
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(16, 16, 16, 16)

        // Contenido
        val contentLayout = LinearLayout(requireContext())
        contentLayout.orientation = LinearLayout.HORIZONTAL

        // Contenido principal
        val contentTextView = TextView(requireContext())
        contentTextView.text = publication.content
        contentTextView.textSize = 20.0F
        contentTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
            weight = 1.0f
        }
        contentLayout.addView(contentTextView)

        // Botón de respuesta
        if (publication.answerContent == "") {
            val replyButton = ImageButton(requireContext())
            replyButton.rotation = -90f
            replyButton.setImageResource(R.drawable.reply_icon) // Replace with your arrow icon
            replyButton.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                marginStart = 8 // Adjust the margin as needed
            }
            replyButton.setOnClickListener {
                answerPublication(publication)
            }
            contentLayout.addView(replyButton)
        }

        linearLayout.addView(contentLayout)

        // Fecha
        val dateTextView = TextView(requireContext())
        val realmInstant = publication.date
        val instant = Instant.ofEpochSecond(realmInstant.epochSeconds, realmInstant.nanosecondsOfSecond.toLong())
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dia = date.dayOfMonth
        val mes = date.monthValue
        val ano = date.year
        val fecha = "$dia/$mes/$ano"
        dateTextView.text = fecha
        dateTextView.textSize = 16.0F // Reduce the font size of the date
        dateTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.START
        }
        linearLayout.addView(dateTextView)

        linearLayout.setOnLongClickListener {
            if (admin) {
                // Mostrar un diálogo de confirmación
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Eliminar publicación")
                builder.setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
                builder.setPositiveButton("Eliminar") { _, _ ->
                    // Eliminar la publicación
                    if (deletePublicationFromMongo(publication._id)) {
                        // Eliminar el layout
                        (linearLayout.parent as ViewGroup).removeView(linearLayout)
                        reloadFragment()
                    }
                }
                builder.setNegativeButton("Cancelar", null)
                builder.show()
            }
            true
        }

        return linearLayout
    }


    private fun createResponseLayout(publication: Followup): View {
        val responseLayout = LinearLayout(requireContext())
        responseLayout.orientation = LinearLayout.VERTICAL
        responseLayout.setPadding(16, 16, 16, 16)
        responseLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 32
        }

        // Contenido de la respuesta
        val responseContentTextView = TextView(requireContext())
        responseContentTextView.text = publication.answerContent
        responseContentTextView.textSize = 18.0F
        responseContentTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.START
        }
        responseLayout.addView(responseContentTextView)

        // Fecha de la respuesta
        val responseDateTextView = TextView(requireContext())
        val realmInstant = publication.answerDate
        val instant = Instant.ofEpochSecond(realmInstant.epochSeconds, realmInstant.nanosecondsOfSecond.toLong())
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dia = date.dayOfMonth
        val mes = date.monthValue
        val ano = date.year
        val fecha = "$dia/$mes/$ano"
        responseDateTextView.text = fecha
        responseDateTextView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.START
            topMargin = 8
        }
        responseLayout.addView(responseDateTextView)

        responseLayout.setOnLongClickListener {
            if (admin) {
                // Mostrar un diálogo de confirmación
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Eliminar respuesta")
                builder.setMessage("¿Estás seguro de que deseas eliminar esta respuesta?")
                builder.setPositiveButton("Eliminar") { _, _ ->
                    // Eliminar la respuesta
                    val eliminado = deleteResponse(publication._id)
                    // Eliminar el layout
                    if(eliminado) {
                        (responseLayout.parent as ViewGroup).removeView(responseLayout)
                        reloadFragment()
                    }
                }
                builder.setNegativeButton("Cancelar", null)
                builder.show()
            }
            true
        }

        return responseLayout
    }

    private fun deletePublication(publication: Followup) {
        // Eliminar la publicación
        // Lógica para eliminar la publicación
    }

    private fun deleteResponse(publicationId: ObjectId) : Boolean {
        try {
            DatabaseConnector.db.writeBlocking {
                val publication = query<Followup>("_id == $0", publicationId).find().first()
                publication.answerContent = ""
            }

            requireContext().toast("Se ha eliminado la respuesta a la publicación")
            return true
        } catch (e: Exception) {
            println("Error: $e")
            requireContext().toast("Ha habido un problema al eliminar la respuesta a la publicación")
            return false
        }
    }
    private fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    private fun deletePublicationFromMongo(_id: ObjectId) : Boolean{
        try {
            var logrado = false
            DatabaseConnector.db.writeBlocking {
                val postQuery = query<Followup>("_id == $0", _id).find()
                if (postQuery.isNotEmpty()) {
                    val fav = postQuery[0]
                    delete(fav)
                    lifecycleScope.launch {
                        Toast.makeText(context, "Se ha borrado la publicació", Toast.LENGTH_SHORT).show()
                    }
                    logrado = true
                } else {
                    lifecycleScope.launch {
                        Toast.makeText(context, "No se ha podido la publicació", Toast.LENGTH_SHORT).show()
                    }
                    println("No borrado")
                }
            }
            return logrado
        } catch (e: Exception) {
            lifecycleScope.launch {
                Toast.makeText(context, "Error al borrar la publicación", Toast.LENGTH_SHORT).show()
            }
            println("Error: $e")
            return false
        }
    }


    private fun addPublication() {
        val fragment = followupForumPublicationFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun reloadFragment() {
        val fragment = followupForumFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.framelayout, fragment)
            .commit()
    }

    private fun answerPublication(publication :Followup) {
        val fragment = followupForumAnswerFragment()
        fragment.publicationId = publication._id
        fragment.publicationContent = publication.content
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
        return inflater.inflate(R.layout.fragment_followup_forum, container, false)
    }


}