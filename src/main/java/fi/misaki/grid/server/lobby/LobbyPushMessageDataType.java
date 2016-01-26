package fi.misaki.grid.server.lobby;

import java.util.Arrays;

/**
 * Message data types for lobby push messages.
 *
 * @author vlumi
 */
public enum LobbyPushMessageDataType {
    /**
     * Initialization message/
     */
    INIT("init"),
    /**
     * User join message.
     */
    JOIN("join"),
    /**
     * User leaving message.
     */
    PART("part"),
    /**
     * Chat message.
     */
    CHAT_MESSAGE("chatMessage"),
    /**
     * User status change message.
     */
    STATUS("status"),
    UNKNOWN("");

    private final String code;

    private LobbyPushMessageDataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static LobbyPushMessageDataType ofCode(String code) {
        return Arrays.asList(LobbyPushMessageDataType.values())
                .stream()
                .filter(value -> value.getCode().equals(code))
                .findAny()
                .orElse(UNKNOWN);

    }

}
