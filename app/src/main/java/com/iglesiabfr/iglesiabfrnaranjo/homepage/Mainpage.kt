package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions.SuggestionsMailbox
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.suggestions.SendSuggestion

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Mainpage.newInstance] factory method to
 * create an instance of this fragment.
 */
class Mainpage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var popupMenu: PopupMenu? = null
    private var isSubMenuShowing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mainpage, container, false)

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
                        startActivity(Intent(activity, SuggestionsMailbox::class.java))
                    } else {
                        startActivity(Intent(activity, SendSuggestion::class.java))
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment mainpage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Mainpage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}