package com.iglesiabfr.iglesiabfrnaranjo.forums.pastor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector


class PastorForumFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseConnector.connect()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addPublicacion = view.findViewById<Button>(R.id.addPublicacionForoPastorBtn)
        addPublicacion.setOnClickListener(){
            addPublication()
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