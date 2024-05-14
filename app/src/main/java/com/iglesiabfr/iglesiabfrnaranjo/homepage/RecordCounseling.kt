package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.RecordAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort


class RecordCounseling : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scheduled_counseling, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val email = DatabaseConnector.getCurrentEmail()
        val records = DatabaseConnector.db.query<CounselingSession>("scheduled == $0 AND user == $1",
            true, DatabaseConnector.getUserData())
            .sort("sessionDateTime", Sort.DESCENDING).find()

        recordAdapter = RecordAdapter(records)
        recyclerView.adapter = recordAdapter
        return view
    }
}
