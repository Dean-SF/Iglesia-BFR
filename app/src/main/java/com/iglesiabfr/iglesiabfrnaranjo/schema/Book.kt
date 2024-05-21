package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class LibraryInventory : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var title: String = ""
    var name: String = ""
    var quantity: Int = 0
    var price: Double = 0.0
}