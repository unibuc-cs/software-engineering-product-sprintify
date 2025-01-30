package com.example.runpath.utils

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object PasswordUtilsNoDeps {
    private const val SALT_LENGTH = 16 // 16 bytes = 128 bits salt
    private const val HASH_ALGORITHM = "SHA-256"

    /**
     * Generate a random salt using SecureRandom
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * Hash (salt + password) with SHA-256.
     *
     * @param salt The salt (byte array)
     * @param password Plaintext password
     * @return Byte array of the hash digest
     */
    fun hashPassword(salt: ByteArray, password: String): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        // 1) feed salt
        digest.update(salt)
        // 2) feed password
        digest.update(password.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }

    /**
     * Convenience function that:
     *  - generates salt,
     *  - hashes password,
     *  - returns "salt:hash" in Base64
     *
     * You'll store the returned string in the database.
     */
    fun generateSaltedHash(password: String): String {
        val salt = generateSalt()
        val hash = hashPassword(salt, password)

        // Convert both salt and hash to Base64
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

        // Combine them in some format, e.g., "salt:hash"
        return "$saltBase64:$hashBase64"
    }

    /**
     * Verifies if a candidate password matches the stored "salt:hash".
     *
     * @param candidatePassword The user-supplied password to check
     * @param storedSaltedHash The string that contains the salt and the hash in "salt:hash" format
     * @return true if they match, false otherwise
     */
    fun verifyPassword(candidatePassword: String, storedSaltedHash: String): Boolean {
        // Split the stored string
        val parts = storedSaltedHash.split(":")
        if (parts.size != 2) {
            // The format is invalid
            return false
        }

        val saltBase64 = parts[0]
        val storedHashBase64 = parts[1]

        // Decode from Base64
        val saltBytes = Base64.decode(saltBase64, Base64.NO_WRAP)
        val storedHashBytes = Base64.decode(storedHashBase64, Base64.NO_WRAP)

        // Hash the candidate password with the same salt
        val candidateHashBytes = hashPassword(saltBytes, candidatePassword)

        // Compare in constant time
        return candidateHashBytes.contentEquals(storedHashBytes)
    }
}
