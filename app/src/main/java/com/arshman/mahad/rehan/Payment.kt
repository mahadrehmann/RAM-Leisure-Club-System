package com.arshman.mahad.rehan

data class Payment(
    val id: String = "",
    val date: String = "",
    val type: String = "",
    val amount: String = ""
)
{
    constructor() : this("", "", "", "")
}