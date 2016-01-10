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
public enum LobbyMessagePayloadType {
    INIT("init"),
    JOIN("join"),
    PART("part"),
    MESSAGE("message"),
    UNKNOWN("");

    private final String code;

    private LobbyMessagePayloadType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LobbyMessagePayloadType ofCode(String code) {
        return Arrays.asList(LobbyMessagePayloadType.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
