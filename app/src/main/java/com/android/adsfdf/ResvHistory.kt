package com.android.adsfdf

import java.time.LocalDate

data class ResvHistory(
    val people: People,
    val resvMoney: Int,
    var checkIn: LocalDate,
    var checkOut: LocalDate,
    val reserveRoom: Int,
)