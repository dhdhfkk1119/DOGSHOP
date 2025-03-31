package com.mysite.jjw.UserRole;

import lombok.Getter;

@Getter
public enum SignRole {
    ADMIN("ROLE_ADMIN"),
    SIGN("ROLE_USER");

    SignRole(String value){
        this.value = value;
    }

    private String value;
}
