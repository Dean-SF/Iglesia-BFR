package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.admin.activities.AdminAct
import com.iglesiabfr.iglesiabfrnaranjo.admin.adminInventoryMaterial.AdminInventoryMaterial
import com.iglesiabfr.iglesiabfrnaranjo.admin.adminLibraryInventory.AdminLibraryInventory
import com.iglesiabfr.iglesiabfrnaranjo.admin.adminSchoolMaterial.AdminSchoolMaterial
import com.iglesiabfr.iglesiabfrnaranjo.admin.cults.AdminCult
import com.iglesiabfr.iglesiabfrnaranjo.admin.emotions.SeeEmotions
import com.iglesiabfr.iglesiabfrnaranjo.admin.events.AdminEvent
import com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions.SuggestionsMailbox
import com.iglesiabfr.iglesiabfrnaranjo.admin.video.AdminVideoAdmin
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.emotions.SendEmotion
import com.iglesiabfr.iglesiabfrnaranjo.suggestions.SendSuggestion

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Adminpage.newInstance] factory method to
 * create an instance of this fragment.
 */
class Adminpage : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_adminpage, container, false)
        val eventBut : Button = view.findViewById(R.id.EventBut)
        val actBut : Button = view.findViewById(R.id.ActBut)
        val cultBut : Button  = view.findViewById(R.id.CultBut)
        val loadVideosBut : Button = view.findViewById(R.id.LoadVideo)
        val adminSchoolBut : Button = view.findViewById(R.id.AdminSchoolBut)
        val libraryBut : Button = view.findViewById(R.id.LibraryBut)
        val materialBut : Button = view.findViewById(R.id.MaterialInventBut)

        val profBut = view.findViewById<ImageView>(R.id.profBut)
        profBut.setOnClickListener {
            if (popupMenu == null || !isSubMenuShowing) {
                showSubMenu(profBut)
            }
        }

        eventBut.setOnClickListener {
            val i = Intent(view.context, AdminEvent::class.java)
            startActivity(i)
        }

        actBut.setOnClickListener {
            val i = Intent(view.context, AdminAct::class.java)
            startActivity(i)
        }

        cultBut.setOnClickListener {
            val i = Intent(view.context, AdminCult::class.java)
            startActivity(i)
        }

        loadVideosBut.setOnClickListener {
            val i = Intent(view.context, AdminVideoAdmin::class.java)
            startActivity(i)
        }

        adminSchoolBut.setOnClickListener {
            val i = Intent(view.context, AdminSchoolMaterial::class.java)
            startActivity(i)
        }

        libraryBut.setOnClickListener {
            val i = Intent(view.context, AdminLibraryInventory::class.java)
            startActivity(i)
        }

        materialBut.setOnClickListener {
            val i = Intent(view.context, AdminInventoryMaterial::class.java)
            startActivity(i)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Adminpage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Adminpage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}