package com.iglesiabfr.iglesiabfrnaranjo.database

import io.realm.kotlin.mongodb.App

object AppConnector {
    val app : App = App.create("iglesiabfr-pigqi")
}