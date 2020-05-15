package com.example.adme.Helpers;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validation {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" + //start of string
                    "(?=.*[0-9])" +  //At least one Number
                    "(?=.*[a-z])" +  //At least one Lowercase letter
                    "(?=.*[A-Z])" +  //At least one uppercase letter
                    "(?=.*[@#$%^&+=])" + //At least one special character
                    "(?=\\S+$)" +  //No Whitespace
                    ".{6,}" + //At least Six Character
                    "$"); //End of String

    public boolean isPasswordValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
