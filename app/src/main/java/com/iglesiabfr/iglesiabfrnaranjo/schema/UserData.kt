package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class UserData : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name : String = ""
    var email : String = ""
    var birthdate: RealmInstant = RealmInstant.now()
    var isAdmin : Boolean = false
}