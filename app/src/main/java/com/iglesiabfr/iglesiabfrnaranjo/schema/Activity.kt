package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class Activity : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name : String = ""
    var date : RealmInstant = RealmInstant.now()
    var desc : String = ""
}