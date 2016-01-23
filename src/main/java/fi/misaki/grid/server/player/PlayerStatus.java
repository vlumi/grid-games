/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.grid.server.player;

import java.util.Arrays;

/**
 * Player status.
 *
 * @author vlumi
 */
public enum PlayerStatus {
    FREE("free"),
    BUSY("busy"),
    UNKNOWN("");

    private PlayerStatus(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static PlayerStatus ofValue(String value) {
        return Arrays.asList(PlayerStatus.values())
                .stream()
                .filter(entry -> entry.getValue().equals(value))
                .findAny()
                .orElse(UNKNOWN);

    }

}
