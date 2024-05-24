package com.iglesiabfr.iglesiabfrnaranjo.schema


import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.ObjectId

class PublicacionForoPastor : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var content: String = ""
    var title:String = ""
    var date : RealmInstant = RealmInstant.now()
}