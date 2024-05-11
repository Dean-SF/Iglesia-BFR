package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import org.mongodb.kbson.ObjectId
import io.realm.kotlin.types.annotations.PrimaryKey

// Clase para representar la asistencia a un evento por parte de un miembro
open class Attendance  : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId() // Id de la asistencia
    var eventId: String = "" // Id del evento al que se asistió
    var memberId: String = "" // Id del miembro que asistió
    var timestamp: RealmInstant = RealmInstant.now() // Marca de tiempo de la asistencia
    var description: String = ""
}