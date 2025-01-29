package com.example.runpath
import com.example.runpath.ui.theme.RegisterLogin.hasSpace
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpaceCheckerTest {

    @Test
    fun testNoSpace() {
        assertTrue(hasSpace("HelloWorld"))     // No space
        assertTrue(hasSpace(""))               // Empty string
        assertTrue(hasSpace("123456"))         // Numeric string with no spaces
        assertTrue(hasSpace("!@#$%^&*()"))     // Special characters, no spaces
    }
    @Test
    fun testHasSpace() {
        assertFalse(hasSpace("Hello World"))     // Space in the middle
        assertFalse(hasSpace(" LeadingSpace"))   // Space at the beginning
        assertFalse(hasSpace("TrailingSpace "))  // Space at the end
        assertFalse(hasSpace("Multi  Spaces"))   // Multiple spaces
    }
}
