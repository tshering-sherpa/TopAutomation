package com.topautomation.api.models;

public record AuthRequest(String username, String password) {

    /** Never print the password into logs or reports. */
    @Override
    public String toString() {
        return "AuthRequest[username=" + username + ", password=***]";
    }
}
