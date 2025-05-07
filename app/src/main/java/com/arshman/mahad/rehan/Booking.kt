package com.arshman.mahad.rehan

data class Booking(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val date: String = "",
    val userId: String = ""
)
{
    constructor() : this("", "", "", "", "", "")
}