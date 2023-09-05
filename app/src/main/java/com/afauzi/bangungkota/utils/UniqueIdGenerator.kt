package com.afauzi.bangungkota.utils

import java.security.SecureRandom

object UniqueIdGenerator {
    private val random = SecureRandom()

    fun generateUniqueId(): String {
        val timestamp = System.currentTimeMillis()
        val randomValue = random.nextInt(100000) // Angka acak dari 0 hingga 99999
        val uniqueId = "${timestamp}_${randomValue}_${generateRandomString(5)}"

        return uniqueId
    }

    private fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val randomString = StringBuilder()

        for (i in 0 until length) {
            val randomIndex = random.nextInt(characters.length)
            randomString.append(characters[randomIndex])
        }

        return randomString.toString()
    }
}