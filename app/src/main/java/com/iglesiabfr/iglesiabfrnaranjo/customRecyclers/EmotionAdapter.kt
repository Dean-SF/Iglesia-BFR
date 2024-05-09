package com.iglesiabfr.iglesiabfrnaranjo.customRecyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.schema.Emotion
import io.realm.kotlin.query.RealmResults
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class EmotionAdapter(private val emotions: RealmResults<Emotion>) :
    RecyclerView.Adapter<EmotionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emotions_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val emotion = emotions[position]
        holder.bind(emotion)
    }

    override fun getItemCount(): Int {
        return emotions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDateTime: TextView = itemView.findViewById(R.id.textDateTime)
        private val emotion: TextView = itemView.findViewById(R.id.textEmotion)
        private val emoticon: ImageView = itemView.findViewById(R.id.emoticon)

        fun bind(newEmotion: Emotion) {
            val datetimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
            val zoneIdCostaRica = ZoneId.of("America/Costa_Rica")
            val epochSeconds = newEmotion.dateRegistered.epochSeconds
            val datetimeCostaRica = LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdCostaRica)
                .toLocalDateTime()

            textDateTime.text = datetimeCostaRica.format(datetimeFormatter)
            emotion.text = newEmotion.emotion
            emoticon.setImageResource(getEmotionImageResource(newEmotion.emotionId))
        }

        private fun getEmotionImageResource(emotionId: Int): Int {
            return when (emotionId) {
                1 -> R.drawable.feliz
                2 -> R.drawable.enojado
                3 -> R.drawable.triste
                4 -> R.drawable.bendecido
                5 -> R.drawable.agradecido
                6 -> R.drawable.pocafe
                7 -> R.drawable.angustiado
                else -> R.drawable.feliz
            }
        }
    }


}