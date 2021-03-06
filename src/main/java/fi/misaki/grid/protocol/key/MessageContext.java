package fi.misaki.grid.protocol.key;

import java.util.Arrays;

/**
 * Contexts for the message envelope, for delegation of the message handling.
 *
 * @author vlumi
 */
public enum MessageContext {
    PLAYER("player"),
    LOBBY("lobby"),
    GAME("game"),
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
