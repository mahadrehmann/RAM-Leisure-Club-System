package com.arshman.mahad.rehan

data class Staff(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val name: String = "",
    var dp: String = "",
    var isSynced: Int = 0    // 0 = not yet sent to Firebase; 1 = sent
) {
    constructor() : this("", "", "", "", "", "", "", 0)
}