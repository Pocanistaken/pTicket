package com.pocan.pticket.checkers;

public class EmailChecker {

    public static boolean isValidEmail(String email) {
        if (email.contains("@")) {
            return true;
        }
        return false;
    }
}
