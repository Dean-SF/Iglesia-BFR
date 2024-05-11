package com.iglesiabfr.iglesiabfrnaranjo.admin.emotions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.EmotionAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Emotion
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort

class SeeEmotions: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_see_emotions)

        val emotionsRecycler: RecyclerView = findViewById(R.id.emotionsList)
        emotionsRecycler.layoutManager = LinearLayoutManager(this)

        val emotions = DatabaseConnector.db.query<Emotion>().sort("dateRegistered", Sort.DESCENDING).find()
        val adapter = EmotionAdapter(emotions)
        emotionsRecycler.adapter = adapter
    }
}