package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class Suggestion : RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var suggestion : String = ""
    var dateSent: RealmInstant = RealmInstant.now()
}