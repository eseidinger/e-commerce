package de.eseidinger.ecommerce.util;

public class InputValidator {
  public static boolean isValidEmail(String email) {
    // Require at least one dot in the domain part, and a valid subdomain
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
  }

  public static boolean isNonEmptyString(String str) {
    return str != null && str.trim().length() > 1;
  }

  public static boolean isValidAddress(String address) {
    return address != null && address.trim().length() > 3;
  }

  public static boolean isValidPrice(Double price) {
    return price != null && price > 0;
  }

  public static boolean isValidRating(Integer rating) {
    return rating != null && rating >= 1 && rating <= 5;
  }

  // Add more validation methods as needed
}
