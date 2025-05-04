package com.arshman.mahad.rehan

data class Admin(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val name: String,
    val dp: String,
)
{
    constructor() : this("", "", "", "", "", "", "")
}