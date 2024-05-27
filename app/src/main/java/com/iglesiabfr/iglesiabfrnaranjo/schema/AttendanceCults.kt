package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// Clase para representar la asistencia a un evento por parte de un miembro
class AttendanceCults : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var namePerson: String = ""
    var timestamp: RealmInstant = RealmInstant.now()
    var eventId: ObjectId = ObjectId()
}

