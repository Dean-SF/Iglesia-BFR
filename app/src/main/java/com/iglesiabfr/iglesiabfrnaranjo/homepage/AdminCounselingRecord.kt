package com.iglesiabfr.iglesiabfrnaranjo.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.AdminRecordAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort

class AdminCounselingRecord : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordAdapter: AdminRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_record_counseling, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val requests = DatabaseConnector.db.query<CounselingSession>("scheduled == $0", true).sort("sessionDateTime", Sort.DESCENDING).find()
        recordAdapter = AdminRecordAdapter(requests)
        recyclerView.adapter = recordAdapter
        return view
    }
}
