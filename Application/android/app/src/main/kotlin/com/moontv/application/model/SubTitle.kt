package com.moontv.application.model

data class SubTitle(
    val id: String,
    val type: String,
    val url: String,
    val image: String,
    val language: String,
    var selected: Boolean = false
) {
}
