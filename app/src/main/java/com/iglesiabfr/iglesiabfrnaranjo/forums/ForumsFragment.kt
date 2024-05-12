package com.iglesiabfr.iglesiabfrnaranjo.forums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.forums.followup.followupForumFragment
import com.iglesiabfr.iglesiabfrnaranjo.forums.pastor.PastorForumFragment
import com.iglesiabfr.iglesiabfrnaranjo.forums.petition.PetitionsForumFragment


class ForumsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnPastor = view.findViewById<Button>(R.id.pastorBtn)
        val btnSeguimiento = view.findViewById<Button>(R.id.seguimientoBtn)
        val btnPeticiones = view.findViewById<Button>(R.id.peticionesBtn)

        btnPastor.setOnClickListener(){
            pastorForum()
        }
        btnPeticiones.setOnClickListener(){
            petitionForum()
        }
        btnSeguimiento.setOnClickListener(){
            followupForum()
        }
    }

    private fun followupForum() {
        val fragment = followupForumFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun petitionForum() {
        val fragment = PetitionsForumFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.framelayout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun pastorForum() {
        val fragment = PastorForumFragment()
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
        return inflater.inflate(R.layout.fragment_forums, container, false)
    }

    
}