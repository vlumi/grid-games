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
public enum PushMessageKey {
    TYPE("type"),
    PAYLOAD("payload"),
    UNKNOWN("");

    private String code;

    private PushMessageKey() {
    }

    private PushMessageKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PushMessageKey ofCode(String code) {
        return Arrays.asList(PushMessageKey.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
