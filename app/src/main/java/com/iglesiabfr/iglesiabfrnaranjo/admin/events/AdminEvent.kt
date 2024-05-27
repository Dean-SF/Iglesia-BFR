package com.iglesiabfr.iglesiabfrnaranjo.admin.events

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iglesiabfr.iglesiabfrnaranjo.R
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.EagListA
import com.iglesiabfr.iglesiabfrnaranjo.customRecyclers.items.EagItemA
import com.iglesiabfr.iglesiabfrnaranjo.database.DatabaseConnector
import com.iglesiabfr.iglesiabfrnaranjo.dialogs.LoadingDialog
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.LinkedList


class AdminEvent : AppCompatActivity() {

    private val events = LinkedList<EagItemA>()
    private lateinit var realm : Realm
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var recyclerView : RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private var key : ObjectId? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_eventos)

        // Initialize Realm
        val config = RealmConfiguration.Builder(schema = setOf(Event::class))
            .name("event.realm")
            .build()
        realm = Realm.open(config)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        val searchInput : EditText = findViewById(R.id.searchinput)

        val createButt : Button = findViewById(R.id.createEventBut)
        recyclerView = findViewById(R.id.eventlist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = createEventList()

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (totalItemCount < lastVisibleItemPosition + 5 && !isLoading) {
                        isLoading = true
                        loadEvents()
                    }

                }
            }
        )

        searchInput.setOnEditorActionListener {_, action, _ ->
            return@setOnEditorActionListener when(action) {
                EditorInfo.IME_ACTION_NEXT -> {
                    true
                }
                EditorInfo.IME_ACTION_DONE -> {
                    clearList()
                    true
                }
                else -> false
            }
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            searchInput.setText("")
            clearList()
        }

        loadEvents()

        createButt.setOnClickListener {
            val i = Intent(this, CreateEvent::class.java)
            launcher.launch(i)
        }
    }

    private fun clearList() {
        key = null
        events.clear()
        recyclerView.adapter = createEventList()
        loadEvents()
    }
    private fun loadEvents() {
        val startIndex = events.size
        val searchInput : TextView = findViewById(R.id.searchinput)
        val eventsFound = if (key == null && searchInput.text.isEmpty()) {
            DatabaseConnector.db.query<Event>().sort("_id").limit(14).find()
        } else if (key == null){
            DatabaseConnector.db.query<Event>("name CONTAINS[c] $0",searchInput.text.toString()).sort("_id").limit(14).find()
        } else if(searchInput.text.isNotEmpty()) {
            DatabaseConnector.db.query<Event>("_id > $0 AND name CONTAINS[c] $1",key,searchInput.text.toString()).sort("_id").limit(14).find()
        } else  {
            DatabaseConnector.db.query<Event>("_id > $0",key).sort("_id").limit(14).find()
        }

        if (eventsFound.isNotEmpty()) key = eventsFound[eventsFound.size-1]._id
        else {
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm a")
        eventsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.date.epochSeconds,0, ZoneOffset.UTC)
            events.add(EagItemA(it._id,it.name,time.format(formatter)))
        }

        recyclerView.post {
            recyclerView.adapter?.notifyItemRangeChanged(startIndex,eventsFound.size)
            isLoading = false
        }

    }

    private fun createEventList() : EagListA {
        val eventList = EagListA(events)
        eventList.onItemClick = {
            val i = Intent(this,DetailEvent::class.java)
            i.putExtra("object_id",it.id.toHexString())
            launcher.launch(i)
        }
        return eventList
    }
}