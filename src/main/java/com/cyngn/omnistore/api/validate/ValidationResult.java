package com.cyngn.omnistore.api.validate;

/**
 * Class to hold the result of request validation
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class ValidationResult {
    public boolean valid;
    public String message;
    public static ValidationResult SUCCESS = new ValidationResult(true, null);

    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
}