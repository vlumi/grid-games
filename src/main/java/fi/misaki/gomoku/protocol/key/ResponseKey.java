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
public enum ResponseKey {
    ERROR("error"),
    MESSAGE("message"),
    UNKNOWN("");

    private String code;

    private ResponseKey() {
    }

    private ResponseKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ResponseKey ofCode(String code) {
        return Arrays.asList(ResponseKey.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }
}
