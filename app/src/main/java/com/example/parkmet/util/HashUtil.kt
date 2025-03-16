package com.example.parkmet.util

import java.security.MessageDigest

object HashUtil {
    // Securely hash a plain-text password using SHA-256
    fun sha256(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}
