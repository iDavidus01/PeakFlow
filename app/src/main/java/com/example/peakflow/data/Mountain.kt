package com.example.peakflow.data

data class Mountain(
    val id: Int,
    val name: String,
    val height: Int,
    val region: String,
    val condReq: Int,
    val techReq: Int,
    val acclReq: Int,
    val riskReq: Int,
    val description: String
)
