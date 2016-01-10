/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.user;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum UserMessagePayloadType {
    LOGIN("login"),
    UNKNOWN("");

    private final String code;

    private UserMessagePayloadType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static UserMessagePayloadType ofCode(String code) {
        return Arrays.asList(UserMessagePayloadType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
