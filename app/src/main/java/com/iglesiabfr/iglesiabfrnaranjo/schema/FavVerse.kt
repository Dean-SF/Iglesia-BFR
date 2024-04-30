package com.iglesiabfr.iglesiabfrnaranjo.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class FavVerse : RealmObject{
    @PrimaryKey
    var _id : ObjectId = ObjectId()
    var chapter : String = ""
    var totalVerses : Int = 1
    var verse : Int = 1
    var owner : String = ""
}
