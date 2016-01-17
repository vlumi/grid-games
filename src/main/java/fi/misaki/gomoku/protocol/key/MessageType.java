package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum MessageType {
    USER("user"),
    LOBBY("lobby"),
    GOMOKU("gomoku"),
    ERROR("error"),
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
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
