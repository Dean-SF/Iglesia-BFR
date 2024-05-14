package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import io.realm.kotlin.query.RealmResults
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// El admin puede ver las sesiones que han solicitado
// Los datos son la fecha de solicitud y el nombre del solicitante
class AdminRequestAdapter(private val requests: RealmResults<CounselingSession>) :
    RecyclerView.Adapter<AdminRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_request_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
        private val textRequest: TextView = itemView.findViewById(R.id.textRequest)

        fun bind(newRequest: CounselingSession) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
            val zoneIdCostaRica = ZoneId.of("America/Costa_Rica")
            val epochSeconds = newRequest.postDatetime.epochSeconds
            val datetimeCostaRica = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdCostaRica)
                .toLocalDateTime()
            textDateTime.text = datetimeCostaRica.format(datetimeFormatter)

            val userName = newRequest.name
            val fullTextRequest = "$userName solicitó una sesión"
            textRequest.text = fullTextRequest
            Log.d("AdminRequestAdapter", "$userName - ${textDateTime.text}")
        }
    }
}