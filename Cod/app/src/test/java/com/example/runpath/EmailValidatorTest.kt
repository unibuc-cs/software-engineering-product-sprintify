package com.example.runpath

import org.junit.Assert.assertEquals
import com.example.runpath.ui.theme.Maps.FormatTime
import com.example.runpath.ui.theme.RegisterLogin.isEmailValid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class EmailValidatorTest {
    @Test
    fun testValidEmails(): Unit {
        assertTrue(isEmailValid("test@example.com")) // Basic valid email
        assertTrue(isEmailValid("user.name@domain.com")) // Email with dot in the username
        assertTrue(isEmailValid("user+tag@domain.co.uk")) // Email with plus and subdomain
        assertTrue(isEmailValid("user@sub.domain.com")) // Email with multiple domain levels
    }

    @Test
    fun testInvalidEmails(): Unit {
        assertFalse(isEmailValid("plainaddress")) // Missing @ and domain
        assertFalse(isEmailValid("@missingusername.com")) // Missing username
        assertFalse(isEmailValid("user@.com")) // Invalid domain
        assertFalse(isEmailValid("user@domain..com")) // Double dot in domain
        assertFalse(isEmailValid("user@domain.com.")) // Trailing dot
        assertFalse(isEmailValid("user@domain,com")) // Comma instead of dot
        assertFalse(isEmailValid("user@domain")) // Missing top-level domain
        assertFalse(isEmailValid("user@-domain.com")) // Leading hyphen in domain
        assertFalse(isEmailValid("user@domain..com")) // Double dot in domain
        assertFalse(isEmailValid("user@domain.c")) // Top-level domain too short
        assertFalse(isEmailValid("user@domain.toolongtld")) // TLD too long
        assertFalse(isEmailValid("user@127.0.0.1")) // IP address as domain (unless allowed)
    }
}