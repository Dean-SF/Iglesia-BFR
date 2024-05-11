package com.iglesiabfr.iglesiabfrnaranjo.admin.suggestions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.SuggestionAdapter
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.schema.Suggestion
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort

class SuggestionsMailbox : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_buzon_sugerencias)

        val suggestionsRecycler: RecyclerView = findViewById(R.id.suggestionsList)
        suggestionsRecycler.layoutManager = LinearLayoutManager(this)

        val suggestions = DatabaseConnector.db.query<Suggestion>().sort("dateSent", Sort.DESCENDING).find()
        val adapter = SuggestionAdapter(suggestions)
        suggestionsRecycler.adapter = adapter
    }
}
