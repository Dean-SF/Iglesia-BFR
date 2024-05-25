package com.iglesiabfr.iglesiabfrnaranjo.database

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.iglesiabfr.iglesiabfrnaranjo.schema.Activity
import com.iglesiabfr.iglesiabfrnaranjo.schema.Attendance
import com.iglesiabfr.iglesiabfrnaranjo.schema.AttendanceCults
import com.iglesiabfr.iglesiabfrnaranjo.schema.CounselingSession
import com.iglesiabfr.iglesiabfrnaranjo.schema.Cult
import com.iglesiabfr.iglesiabfrnaranjo.schema.Emotion
import com.iglesiabfr.iglesiabfrnaranjo.schema.Event
import com.iglesiabfr.iglesiabfrnaranjo.schema.FavVerse
import com.iglesiabfr.iglesiabfrnaranjo.schema.Followup
import com.iglesiabfr.iglesiabfrnaranjo.schema.InventoryMaterial
import com.iglesiabfr.iglesiabfrnaranjo.schema.LibraryInventory
import com.iglesiabfr.iglesiabfrnaranjo.schema.Petition
import com.iglesiabfr.iglesiabfrnaranjo.schema.PublicacionForoPastor
import com.iglesiabfr.iglesiabfrnaranjo.schema.SchoolMaterial
import com.iglesiabfr.iglesiabfrnaranjo.schema.Suggestion
import com.iglesiabfr.iglesiabfrnaranjo.schema.UserData
import com.iglesiabfr.iglesiabfrnaranjo.schema.Video
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.launch

object DatabaseConnector {
    lateinit var db : Realm
    var email = ""
    private var isAdmin : Boolean = false
    lateinit var credentials: Credentials
    private var userData: UserData? = null

    private var onFinished: ((Boolean) -> Unit)? = null

    fun getLogCurrent() : User {
        return AppConnector.app.currentUser!!
    }

    fun getCurrentEmail() : String {
        return email
    }

    fun getIsAdmin() : Boolean {
        return isAdmin
    }

    fun setIsAdmin() {
        this.isAdmin = getUserData()?.isAdmin == true
    }

    fun getUserData(): UserData? {
        return userData
    }

    fun setUserData() {
        userData = getLogCurrent().let { db.query<UserData>("email == $0", email).find().firstOrNull() }
    }

    private suspend fun logAnonymous(): User {
        return AppConnector.app.login(Credentials.anonymous(reuseExisting = true))
    }

    fun setOnFinishedListener(listener : ((Boolean)->Unit)) : DatabaseConnector {
        onFinished = listener
        return this
    }
    fun connectForRegister(lifecyclescope: LifecycleCoroutineScope) {
        lifecyclescope.launch {
            runCatching {
                logAnonymous()
            }.onSuccess {
                Log.d("Info", "Sync Started")
                val config = SyncConfiguration.Builder(
                    it, setOf(
                        UserData::class,
                    ))
                    .initialSubscriptions(rerunOnOpen = true) {realm->
                        add(realm.query<UserData>(), "userData",updateExisting = true)
                    }
                    .errorHandler { session: SyncSession, error: SyncException ->
                        Log.d("IglesiaError", error.message.toString())
                    }
                    .build()
                db = Realm.open(config)
                db.subscriptions.waitForSynchronization()
                onFinished?.invoke(true)
                Log.d("IglesiaInfo", "Sync Finished")
            }.onFailure {
                onFinished?.invoke(false)
                Log.d("IglesiaError", it.message.toString())
            }
        }
    }
    fun connect(lifecyclescope: LifecycleCoroutineScope) {
        lifecyclescope.launch {
            runCatching {
                getLogCurrent()
            }.onSuccess {
                Log.d("Info", "Sync Started")
                val config = SyncConfiguration.Builder(
                    it, setOf(
                        Event::class,
                        Activity::class,
                        UserData::class,
                        Cult::class,
                        Attendance::class,
                        AttendanceCults::class,
                        Suggestion::class,
                        Emotion::class,
                        CounselingSession::class,
                        FavVerse::class,
                        Followup::class,
                        Petition::class,
                        PublicacionForoPastor::class,
                        Video::class,
                        LibraryInventory::class,
                        SchoolMaterial::class,
                        InventoryMaterial::class
                    ))
                    .initialSubscriptions(rerunOnOpen = true) {realm->
                        add(realm.query<Event>(), "subEvent",updateExisting = true)
                        add(realm.query<Cult>(), "subCult",updateExisting = true)
                        add(realm.query<Activity>(), "subActivity",updateExisting = true)
                        add(realm.query<UserData>(), "userData",updateExisting = true)
                        add(realm.query<Attendance>(), "attendance",updateExisting = true)
                        add(realm.query<AttendanceCults>(), "attendanceCults",updateExisting = true)
                        add(realm.query<Suggestion>(), "suggestion",updateExisting = true)
                        add(realm.query<Emotion>(), "emotion",updateExisting = true)
                        add(realm.query<CounselingSession>(), "counsellingSession",updateExisting = true)
                        add(realm.query<FavVerse>(), "FavVerse",updateExisting = true)
                        add(realm.query<Followup>(), "Followup",updateExisting = true)
                        add(realm.query<Petition>(), "Petition",updateExisting = true)
                        add(realm.query<Video>(), "video",updateExisting = true)
                        add(realm.query<LibraryInventory>(), "libraryInventory",updateExisting = true)
                        add(realm.query<SchoolMaterial>(), "schoolMaterial",updateExisting = true)
                        add(realm.query<InventoryMaterial>(), "inventoryMaterial",updateExisting = true)
                    }
                    .errorHandler { session: SyncSession, error: SyncException ->
                        Log.d("IglesiaError", error.message.toString())
                    }
                    //.waitForInitialRemoteData()
                    .build()
                db = Realm.open(config)
                db.subscriptions.waitForSynchronization()
                onFinished?.invoke(true)
                Log.d("IglesiaInfo", "Sync Finished")
            }.onFailure {
                onFinished?.invoke(false)
                Log.d("IglesiaError", it.message.toString())
            }
        }
    }
}