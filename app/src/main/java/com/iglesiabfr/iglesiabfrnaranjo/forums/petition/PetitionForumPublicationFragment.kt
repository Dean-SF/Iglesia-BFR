package com.iglesiabfr.iglesiabfrnaranjo.forums.petition

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Petition


class PetitionForumPublicationFragment : Fragment() {
    private val options = arrayOf("Petición", "Oración", "Testimonio")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_petition_forum_publication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner = view.findViewById<Spinner>(R.id.pettionTypeSelector)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val button = view.findViewById<Button>(R.id.publishPetition)
        val publicationText = view.findViewById<TextInputEditText>(R.id.petitionPublicationText)
        val publicationContent = publicationText.text
        button.setOnClickListener {
            val selectedOption = spinner.selectedItem as String
            val content = publicationContent.toString().trim()

            if (content.isEmpty()) {
                requireContext().toast("El contenido de la publicación no debe estar vacío")
            } else {
                addPublication(content, selectedOption)
            }
        }
    }

    private fun addPublication(publicationContent: String, petitionType: String) {
        try {
            val publication = Petition().apply{
                content = publicationContent
                type = petitionType
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


}