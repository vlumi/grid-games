/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.gomoku;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum GomokuMessageType {

    STATE("state"),
    UNKNOWN("");

    private final String code;

    private GomokuMessageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GomokuMessageType ofCode(String code) {
        return Arrays.asList(GomokuMessageType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
