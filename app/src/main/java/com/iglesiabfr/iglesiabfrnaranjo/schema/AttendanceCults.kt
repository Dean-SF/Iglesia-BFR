package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// Clase para representar la asistencia a un evento por parte de un miembro
class AttendanceCults : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId() // Id de la asistencia
    var eventId: String = "" // Id del evento al que se asistió
    var memberId: String = "" // Id del miembro que asistió
    var timestamp: RealmInstant = RealmInstant.now() // Marca de tiempo de la asistencia
    var eventPresent: String = ""
}

