/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum MessageType {

    AUTH("auth"),
    LOBBY("lobby"),
    GOMOKU("gomoku"),
    UNKNOWN("");

    private final String code;

    private MessageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MessageType ofCode(String code) {
        return Arrays.asList(MessageType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
