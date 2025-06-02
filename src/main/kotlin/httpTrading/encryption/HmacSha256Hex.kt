package com.example.httpTrading.encryption

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmacSha256Hex(
    data: String,
    key: String,
): String {
    val algorithm = "HmacSHA256"
    val mac = Mac.getInstance(algorithm)
    val secretKeySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), algorithm)
    mac.init(secretKeySpec)
    val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
    return hmacBytes.joinToString("") { "%02x".format(it) }
}
