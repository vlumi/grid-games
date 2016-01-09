/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.server.lobby;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum LobbyMessageType {

    JOIN("join"),
    PART("part"),
    MESSAGE("message"),
    UNKNOWN("");

    private final String code;

    private LobbyMessageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LobbyMessageType ofCode(String code) {
        return Arrays.asList(LobbyMessageType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
