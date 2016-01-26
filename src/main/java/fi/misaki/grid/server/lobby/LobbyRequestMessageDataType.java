package fi.misaki.grid.server.lobby;

import java.util.Arrays;

/**
 * Message data types for lobby request messages.
 *
 * @author vlumi
 */
public enum LobbyRequestMessageDataType {
    /**
     * Chat message.
     */
    CHAT_MESSAGE("chatMessage"),
    UNKNOWN("");

    private final String code;

    private LobbyRequestMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LobbyRequestMessageDataType ofCode(String code) {
        return Arrays.asList(LobbyRequestMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
