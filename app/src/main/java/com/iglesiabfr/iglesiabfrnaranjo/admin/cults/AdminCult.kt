package com.iglesiabfr.iglesiabfrnaranjo.admin.cults

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
import com.iglesiabfr.iglesiabfrnaranjo.homepage.Homepage
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.LinkedList


class AdminCult : AppCompatActivity() {

    private val cults = LinkedList<EagItemA>()
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var recyclerView : RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private var key : ObjectId? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cult)

        DatabaseConnector.initialize(this)

        // Initialize loading dialog
        loadingDialog = LoadingDialog(this)

        val searchInput : EditText = findViewById(R.id.searchinput)

        // Initialize views
        val createButt: Button = findViewById(R.id.createAdminCultBut)
        val backBtn: Button = findViewById(R.id.BackAdminCultButton)
        recyclerView = findViewById(R.id.cultlist)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = createCultList()

        // Add scroll listener to load more items when reaching the end
        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (totalItemCount < lastVisibleItemPosition + 5 && !isLoading) {
                        isLoading = true
                        loadCults()
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

        // Load initial cults
        loadCults()

        createButt.setOnClickListener {
            val i = Intent(this,CreateCult::class.java)
            launcher.launch(i)
        }

        backBtn.setOnClickListener {
            val i = Intent(this, Homepage::class.java)
            launcher.launch(i)
        }
    }

    private fun clearList() {
        key = null
        cults.clear()
        recyclerView.adapter = createCultList()
        loadCults()
    }
    private fun loadCults() {
        val startIndex = cults.size
        val searchInput : TextView = findViewById(R.id.searchinput)
        val cultsFound = if (key == null && searchInput.text.isEmpty()) {
            DatabaseConnector.db.query<Cult>().sort("_id").limit(14).find()
        } else if (key == null){
            DatabaseConnector.db.query<Cult>("name CONTAINS[c] $0",searchInput.text.toString()).sort("_id").limit(14).find()
        } else if(searchInput.text.isNotEmpty()) {
            DatabaseConnector.db.query<Cult>("_id > $0 AND name CONTAINS[c] $1",key,searchInput.text.toString()).sort("_id").limit(14).find()
        } else  {
            DatabaseConnector.db.query<Cult>("_id > $0",key).sort("_id").limit(14).find()
        }
        if (cultsFound.isNotEmpty()) key = cultsFound[cultsFound.size-1]._id
        else {
            return
        }

        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val weekdays = resources.getStringArray(R.array.createCultWeekdays)
        cultsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.time.epochSeconds,0, ZoneOffset.UTC)
            cults.add(EagItemA(it._id,it.name,weekdays[it.weekDay] + " " + time.format(formatter)))
        }

        recyclerView.post {
            recyclerView.adapter?.notifyItemRangeChanged(startIndex,cultsFound.size)
            isLoading = false
        }

    }

    private fun createCultList() : EagListA {
        val cultList = EagListA(cults)
        cultList.onItemClick = {
            val i = Intent(this,DetailCult::class.java)
            i.putExtra("object_id",it.id.toHexString())
            launcher.launch(i)
        }
        return cultList
    }
}

