/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vlumi
 */
public enum MessageKey {
    TYPE("type"),
    PAYLOAD("payload"),
    UNKNOWN("");

    private String code;

    private MessageKey() {
    }

    private MessageKey(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MessageKey ofCode(String code) {
        return Arrays.asList(MessageKey.values())
                .stream()
                .filter(o -> o.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

    public static List<MessageKey> getValidValues() {
        return Arrays.asList(MessageKey.values()).stream()
                .filter(o -> o != MessageKey.UNKNOWN)
                .collect(Collectors.toList());
    }

}
