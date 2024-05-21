package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class SchoolMaterial : RealmObject  {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var teacherName: String = ""
    var clase: String = ""
    var initialMonth: RealmInstant = RealmInstant.now()
    var finalMonth: RealmInstant = RealmInstant.now()
}