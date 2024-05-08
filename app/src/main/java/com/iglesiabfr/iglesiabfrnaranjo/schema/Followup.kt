package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Followup : RealmObject {
    @PrimaryKey
    var _id : ObjectId = ObjectId()
    var answer : Followup? = null
    var content : String = ""
    var date : RealmInstant = RealmInstant.now()
}