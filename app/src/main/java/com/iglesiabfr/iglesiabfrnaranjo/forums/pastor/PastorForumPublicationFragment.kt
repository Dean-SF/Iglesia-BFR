package com.iglesiabfr.iglesiabfrnaranjo.forums.pastor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.PublicacionForoPastor


class PastorForumPublicationFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val publicationText = view.findViewById<TextInputEditText>(R.id.pastorsPublicationText)
        val publicationContent = publicationText.text
        val publicationTitleText = view.findViewById<TextInputEditText>(R.id.pastorForumPublicationTitle)
        val publicationTitleContent = publicationTitleText.text
        val btnPublicar = view.findViewById<Button>(R.id.publicarForoPastorBtn)
        btnPublicar.setOnClickListener {
            val title = publicationTitleContent.toString().trim()
            val content = publicationContent.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                if (title.isEmpty()) {
                    requireContext().toast("El título de la publicación no ouede estar vacío")
                }
                if (content.isEmpty()) {
                    requireContext().toast("El contenido de la publicación no ouede estar vacío")
                }
            } else {
                addPublication(content, title)
            }
        }


    }

    private fun addPublication(publicationContent: String,pubicationTitle: String) {
        try {
            val publication = PublicacionForoPastor().apply{
                content = publicationContent
                title = pubicationTitle
            }
            DatabaseConnector.db.writeBlocking {
                copyToRealm(publication)
            }
            requireContext().toast("Se ha añadido la publicación")
            requireActivity().onBackPressed()
        }catch (e : Exception){
            println("Error: $e")
            requireContext().toast("Ha habido un problema al añadir la publicación")
        }
    }

    private fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pastor_forum_publication, container, false)
    }


}