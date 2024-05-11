package com.iglesiabfr.iglesiabfrnaranjo.schema

import java.time.LocalDate

data class EventData(
    val eventName: String,
    val eventDate: LocalDate,
    val eventTime: String,
    val eventDescription: String
)
