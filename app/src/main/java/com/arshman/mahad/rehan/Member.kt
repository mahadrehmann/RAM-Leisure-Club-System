package com.arshman.mahad.rehan

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val name: String,
    val dp: String,
    val membership: String
)
{
    constructor() : this("", "", "", "", "", "", "", "")
}