package com.ecommerce.jsf.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {
    @Test
    public void testValidEmail() {
        assertTrue(InputValidator.isValidEmail("user@example.com"));
        assertFalse(InputValidator.isValidEmail("user@.com"));
        assertFalse(InputValidator.isValidEmail("user.com"));
        assertFalse(InputValidator.isValidEmail(null));
    }

    @Test
    public void testValidName() {
        assertTrue(InputValidator.isNonEmptyString("John"));
        assertFalse(InputValidator.isNonEmptyString("J"));
        assertFalse(InputValidator.isNonEmptyString("") );
        assertFalse(InputValidator.isNonEmptyString(null));
    }

    @Test
    public void testValidAddress() {
        assertTrue(InputValidator.isValidAddress("123 Main St"));
        assertFalse(InputValidator.isValidAddress("St"));
        assertFalse(InputValidator.isValidAddress("") );
        assertFalse(InputValidator.isValidAddress(null));
    }

    @Test
    public void testValidPrice() {
        assertTrue(InputValidator.isValidPrice(10.0));
        assertTrue(InputValidator.isValidPrice(0.0));
        assertFalse(InputValidator.isValidPrice(-1.0));
        assertFalse(InputValidator.isValidPrice(null));
    }

    @Test
    public void testValidRating() {
        assertTrue(InputValidator.isValidRating(1));
        assertTrue(InputValidator.isValidRating(5));
        assertFalse(InputValidator.isValidRating(0));
        assertFalse(InputValidator.isValidRating(6));
        assertFalse(InputValidator.isValidRating(null));
    }
}
