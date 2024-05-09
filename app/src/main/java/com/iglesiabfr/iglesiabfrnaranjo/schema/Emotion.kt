package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class Emotion: RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var emotion : String = ""
    var emotionId : Int = 0
    var dateRegistered: RealmInstant = RealmInstant.now()
}