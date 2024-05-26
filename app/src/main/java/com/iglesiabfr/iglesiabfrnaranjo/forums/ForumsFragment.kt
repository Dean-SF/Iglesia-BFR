package com.iglesiabfr.iglesiabfrnaranjo.forums

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.emotions.SeeEmotions
import com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions.SuggestionsMailbox
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.emotions.SendEmotion
import com.iglesiabfr.iglesiabfrnaranjo.forums.followup.followupForumFragment
import com.iglesiabfr.iglesiabfrnaranjo.forums.pastor.PastorForumFragment
import com.iglesiabfr.iglesiabfrnaranjo.forums.petition.PetitionsForumFragment
import com.iglesiabfr.iglesiabfrnaranjo.homepage.MyProfile
import com.iglesiabfr.iglesiabfrnaranjo.suggestions.SendSuggestion


class ForumsFragment : Fragment() {

    private var popupMenu: PopupMenu? = null
    private var isSubMenuShowing: Boolean = false
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
        val view = inflater.inflate(R.layout.fragment_forums, container, false)
        val profBut = view.findViewById<ImageView>(R.id.profBut)
        profBut.setOnClickListener {
            if (popupMenu == null || !isSubMenuShowing) {
                showSubMenu(profBut)
            }
        }
        return view
    }

    private fun showSubMenu(view: View) {
        popupMenu = PopupMenu(requireContext(), view)
        popupMenu!!.menuInflater.inflate(R.menu.submenu_profile, popupMenu!!.menu)
        popupMenu!!.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(activity, MyProfile::class.java))
                    true
                }
                R.id.menu_emotion_registration -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SeeEmotions::class.java))
                    } else {
                        startActivity(Intent(activity, SendEmotion::class.java))
                    }
                    true
                }
                R.id.menu_suggestion_box -> {
                    if (DatabaseConnector.getIsAdmin()) {
                        startActivity(Intent(activity, SuggestionsMailbox::class.java))
                    } else {
                        startActivity(Intent(activity, SendSuggestion::class.java))
                    }
                    true
                }
                else -> false
            }
        }
        popupMenu!!.setOnDismissListener {
            isSubMenuShowing = false
        }
        popupMenu!!.show()
        isSubMenuShowing = true
    }
}