package com.afauzi.bangungkota.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    var isAuthenticated: Boolean = false,
    var isNew: Boolean? = false,
    var isCreated: Boolean = false
): java.io.Serializable {
    constructor() : this("","","") {

    }
}
