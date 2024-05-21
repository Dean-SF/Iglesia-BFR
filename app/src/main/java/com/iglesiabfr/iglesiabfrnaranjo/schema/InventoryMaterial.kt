package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class InventoryMaterial: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var quantity: Int = 0
    var type: String = ""
}