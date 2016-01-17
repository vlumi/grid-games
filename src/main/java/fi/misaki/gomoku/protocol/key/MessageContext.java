package fi.misaki.gomoku.protocol.key;

import java.util.Arrays;

/**
 *
 * @author vlumi
 */
public enum MessageContext {
    USER("user"),
    LOBBY("lobby"),
    GOMOKU("gomoku"),
    ERROR("error"),
    UNKNOWN("");

    private final String code;

    private MessageContext(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MessageContext ofCode(String code) {
        return Arrays.asList(MessageContext.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
