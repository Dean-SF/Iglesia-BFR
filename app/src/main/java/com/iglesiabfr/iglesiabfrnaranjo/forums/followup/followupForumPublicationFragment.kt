package com.iglesiabfr.iglesiabfrnaranjo.forums.followup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Followup
import com.iglesiabfr.iglesiabfrnaranjo.schema.PublicacionForoPastor

class followupForumPublicationFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val publicationText = view.findViewById<TextInputEditText>(R.id.followupPublicationText)
        val publicationContent = publicationText.text
        val btnPublicar = view.findViewById<Button>(R.id.publishFollowupBtn)
        btnPublicar.setOnClickListener(){
            addPublication(publicationContent.toString())
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