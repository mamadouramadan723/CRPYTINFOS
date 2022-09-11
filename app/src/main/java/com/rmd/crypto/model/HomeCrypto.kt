package com.rmd.crypto.model

data class HomeCrypto(
    var id: String = "",
    var name: String = "",
    var symbol: String = "",
    var price: Double = 0.00,
    var percent_change_1h: Double = 0.00,
    var percent_change_24h: Double = 0.00,
    var percent_change_7d: Double = 0.00
)