package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Suggestion
import io.realm.kotlin.query.RealmResults
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SuggestionAdapter(private val suggestions: RealmResults<Suggestion>) :
    RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.suggestion_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.bind(suggestion)
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
        private val suggestionText: TextView = itemView.findViewById(R.id.textDescription)

        fun bind(suggestion: Suggestion) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm")
            val zoneIdCostaRica = ZoneId.of("America/Costa_Rica")
            val epochSeconds = suggestion.dateSent.epochSeconds
            val datetimeCostaRica = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdCostaRica)
                .toLocalDateTime()

            textDateTime.text = datetimeCostaRica.format(datetimeFormatter)
            suggestionText.text = suggestion.suggestion
        }
    }
}
