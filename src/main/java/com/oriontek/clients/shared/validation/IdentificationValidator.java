package com.oriontek.clients.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdentificationValidator implements ConstraintValidator<ValidIdentification, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String digits = value.replaceAll("[^0-9]", "");
        return switch (digits.length()) {
            case 9 -> true;
            case 11 -> isValidCedula(digits);
            default -> false;
        };
    }

    private boolean isValidCedula(String digits) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int weight = (i % 2 == 0) ? 1 : 2;
            int product = (digits.charAt(i) - '0') * weight;
            if (product > 9) {
                product -= 9;
            }
            sum += product;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == (digits.charAt(10) - '0');
    }
}
