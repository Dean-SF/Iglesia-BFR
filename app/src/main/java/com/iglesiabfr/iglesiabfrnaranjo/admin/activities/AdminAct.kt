package com.iglesiabfr.iglesiabfrnaranjo.admin.activities

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
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.LinkedList


class AdminAct : AppCompatActivity() {

    private val acts = LinkedList<EagItemA>()
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var recyclerView : RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private var key : ObjectId? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_act)

        loadingDialog = LoadingDialog(this)

        val searchInput : EditText = findViewById(R.id.searchinput)

        val createButt : Button = findViewById(R.id.createActBut)
        recyclerView = findViewById(R.id.actlist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = createActList()

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (totalItemCount < lastVisibleItemPosition + 5 && !isLoading) {
                        isLoading = true
                        loadActs()
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


        loadActs()

        createButt.setOnClickListener {
            val i = Intent(this,CreateAct::class.java)
            launcher.launch(i)
        }
    }

    private fun clearList() {
        key = null
        acts.clear()
        recyclerView.adapter = createActList()
        loadActs()
    }
    private fun loadActs() {
        val startIndex = acts.size
        val searchInput : TextView = findViewById(R.id.searchinput)
        val actsFound  = if (key == null && searchInput.text.isEmpty()) {
            DatabaseConnector.db.query<Activity>().sort("_id").limit(14).find()
        } else if (key == null){
            DatabaseConnector.db.query<Activity>("name CONTAINS[c] $0",searchInput.text.toString()).sort("_id").limit(14).find()
        } else if(searchInput.text.isNotEmpty()) {
            DatabaseConnector.db.query<Activity>("_id > $0 AND name CONTAINS[c] $1",key,searchInput.text.toString()).sort("_id").limit(14).find()
        } else  {
            DatabaseConnector.db.query<Activity>("_id > $0",key).sort("_id").limit(14).find()
        }

        if (actsFound.isNotEmpty()) key = actsFound[actsFound.size-1]._id
        else {
            return
        }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy hh:mm a")
        actsFound.map {
            val time = LocalDateTime.ofEpochSecond(it.date.epochSeconds,0, ZoneOffset.UTC)
            acts.add(EagItemA(it._id,it.name,time.format(formatter)))
        }

        recyclerView.post {
            recyclerView.adapter?.notifyItemRangeChanged(startIndex,actsFound.size)
            isLoading = false
        }

    }

    private fun createActList() : EagListA {
        val actList = EagListA(acts)
        actList.onItemClick = {
            val i = Intent(this,DetailAct::class.java)
            i.putExtra("object_id",it.id.toHexString())
            launcher.launch(i)
        }
        return actList
    }
}