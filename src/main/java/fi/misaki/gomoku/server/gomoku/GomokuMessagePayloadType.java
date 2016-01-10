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
public enum GomokuMessagePayloadType {
    STATE("state"),
    JOIN(""),
    CHALLENGE("challenge"),
    ACCEPT("accept"),
    REJECT("reject"),
    MOVE("move"),
    UNKNOWN("");

    private final String code;

    private GomokuMessagePayloadType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static GomokuMessagePayloadType ofCode(String code) {
        return Arrays.asList(GomokuMessagePayloadType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
