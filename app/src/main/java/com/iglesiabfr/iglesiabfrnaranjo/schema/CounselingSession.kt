package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class CounselingSession : RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var user : UserData? = null
    var postDateTime: RealmInstant = RealmInstant.now()
    var sessionDateTime: RealmInstant = RealmInstant.now()
    var scheduled : Boolean = false
}