package com.vibehopper.domain.user;
public enum Role { USER, ADMIN; public String authority() { return "ROLE_" + name(); } }
