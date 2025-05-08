package com.arshman.mahad.rehan

data class FacilityMaintenance(
    val id: String = "",
    val facility: String = "",
    val dueDate: String = "",    // format: YYYY-MM-DD
    var isSynced: Int = 0        // 0 = not yet uploaded; 1 = uploaded
) {
    constructor() : this("", "", "", 0)
}
