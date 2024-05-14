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

// El admin puede ver las sesiones que ha agendado
// Puede ver la fecha, hora de las sesiones y el nombre del solicitante
class AdminRecordAdapter(private val records: RealmResults<CounselingSession>) :
    RecyclerView.Adapter<AdminRecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_record_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = records[position]
        holder.bind(request)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        private val textName: TextView = itemView.findViewById(R.id.textName)

        fun bind(newRecord: CounselingSession) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            val zoneIdCostaRica = ZoneId.of("America/Costa_Rica")
            val epochSeconds = newRecord.sessionDateTime.epochSeconds
            val datetimeCostaRica = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdCostaRica)
                .toLocalDateTime()

            textDate.text = datetimeCostaRica.format(dateFormatter)
            textTime.text = datetimeCostaRica.format(timeFormatter)
            textName.text = newRecord.name
            Log.d("AdminRecordAdapter", "$newRecord.name - ${textDate.text}")
        }
    }
}