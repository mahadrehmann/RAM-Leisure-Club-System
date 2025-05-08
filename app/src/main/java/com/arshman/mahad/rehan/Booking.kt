package com.arshman.mahad.rehan

// Booking.kt
data class Booking(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val date: String = "",
    val userId: String = "",
    var isSynced: Int = 0      // 0 = not yet uploaded, 1 = uploaded
) {
    constructor() : this("", "", "", "", "", "", 0)
}
