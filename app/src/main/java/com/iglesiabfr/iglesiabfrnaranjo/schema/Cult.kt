package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.util.Date

class Cult : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name : String = ""
    var time : RealmInstant = RealmInstant.now()
    var weekDay : Int = 0
    var desc : String = ""
    var cancelDate : RealmInstant = RealmInstant.from(0,0)
}