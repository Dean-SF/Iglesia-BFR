package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class EventCult : RealmObject {
    @PrimaryKey
    var _id: String = ""
    var name : String = ""
    var date : RealmInstant = RealmInstant.now()
    var time : String = ""
    var desc : String = ""
}

