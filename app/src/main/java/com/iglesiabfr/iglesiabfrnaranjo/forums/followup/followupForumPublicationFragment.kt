package com.iglesiabfr.iglesiabfrnaranjo.forums.followup

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
import com.iglesiabfr.iglesiabfrnaranjo.schema.Followup

class followupForumPublicationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val publicationText = view.findViewById<TextInputEditText>(R.id.followupPublicationText)
        val publicationContent = publicationText.text
        val btnPublicar = view.findViewById<Button>(R.id.publishFollowupBtn)
        btnPublicar.setOnClickListener {
            val content = publicationContent.toString().trim()
            if (content.isEmpty()) {
                requireContext().toast("El contenido de la publicación no debe estar vacío")
            } else {
                addPublication(content)
            }
        }

    }

    private fun addPublication(publicationContent: String) {
        try {
            val publication = Followup().apply{
                content = publicationContent
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
        return inflater.inflate(R.layout.fragment_followup_forum_publication, container, false)
    }
}