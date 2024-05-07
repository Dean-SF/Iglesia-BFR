package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Petition : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var content: String = ""
    var date : RealmInstant = RealmInstant.now()
    var type: String = ""
}