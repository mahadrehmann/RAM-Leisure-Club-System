package com.arshman.mahad.rehan

data class User(
    val id: String = "",            // local‐only UUID until remote signup
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val name: String = "",
    val dp: String = "",
    val membership: String = "",
    var isSynced: Int = 0           // 0 = not yet sent to Firebase; 1 = sent
)
