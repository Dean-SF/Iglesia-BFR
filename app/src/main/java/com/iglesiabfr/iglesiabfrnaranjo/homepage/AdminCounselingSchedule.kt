package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.AdminRequestAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort

class AdminCounselingSchedule : Fragment(), AdminRequestAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestAdapter: AdminRequestAdapter
    private lateinit var refreshbtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_schedule_counseling, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val requests = DatabaseConnector.db.query<CounselingSession>("scheduled == $0", false).sort("postDateTime", Sort.DESCENDING).find()
        requestAdapter = AdminRequestAdapter(requests, this)
        recyclerView.adapter = requestAdapter

        refreshbtn = view.findViewById(R.id.refreshBtn)
        refreshbtn.setOnClickListener {
            refreshRecycler()
        }
        return view
    }

    override fun onItemClick(item: CounselingSession) {
        val sessionId = item._id.toHexString()
        val intent = Intent(requireContext(), AdminCounselingScheduling::class.java)
        intent.putExtra("sessionId", sessionId)
        startActivity(intent)
    }

    private fun refreshRecycler() {
        startRefreshAnimation(refreshbtn)
        val requests = DatabaseConnector.db.query<CounselingSession>("scheduled == $0", false).sort("postDateTime", Sort.DESCENDING).find()
        requestAdapter = AdminRequestAdapter(requests, this)
        recyclerView.adapter = requestAdapter
        stopRefreshAnimation(refreshbtn)
    }

    // Funcion para activar animacion de boton de refrescar
    fun startRefreshAnimation(imageButton: ImageButton) {
        val rotation = ObjectAnimator.ofFloat(imageButton, "rotation", 0f, 360f)
        rotation.duration = 500 // Duracion de la animacion en milisegundos
        rotation.interpolator = LinearInterpolator()

        rotation.start()

        // Solo hace la animacion 1 vez y se detiene
        rotation.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            if (animatedValue >= 360f) {
                stopRefreshAnimation(imageButton)
            }
        }
    }

    // Funcion para detener la animacion del boton de refrescar
    fun stopRefreshAnimation(imageButton: ImageButton) {
        imageButton.clearAnimation()
    }

}
